package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.awt.KeyboardFocusManager;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Usuario;
import com.mycompany.sistemadegestaoeagendamentodesalas.service.DatabaseTaskExecutor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

abstract class PerfilDashboardUI extends JPanel {
	protected final String email;
	protected final int utilizadorId;
	protected final JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
	private final Runnable voltarMenu;
	private final String perfil;
	private JLabel accountLabel;
	private JLabel greetingLabel;
	private final Usuario conta;
	private JDialog formularioAtivo;
	private JButton guardarFormularioAtivo;
	private JButton cancelarFormularioAtivo;
	private boolean formularioSubmetido;

	/** Inicializa a estrutura comum dos paineis por perfil. */
	PerfilDashboardUI(String perfil, String email, int utilizadorId, Runnable voltarMenu) {
		super(new BorderLayout());
		this.email = email;
		this.utilizadorId = utilizadorId;
		this.voltarMenu = voltarMenu;
		this.perfil = perfil;
		UIManager.put("Button.background", PaletaCores.DESTAQUE);
		UIManager.put("Button.foreground", Color.WHITE);
		UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
		conta = new AutenticacaoDAO().buscarConta(perfil, utilizadorId);
		if (conta == null) {
			throw new IllegalStateException("Nao foi possivel carregar a conta autenticada.");
		}
		setBackground(PaletaCores.FUNDO);
		add(criarCabecalho(perfil), BorderLayout.NORTH);
		tabs.setBackground(PaletaCores.SUPERFICIE);
		tabs.setForeground(PaletaCores.TEXTO);
	}

	/** Finaliza o painel com o perfil indicado e sem aviso adicional. */
	protected final void finalizarDashboard(String perfil) {
		finalizarDashboard(perfil, null);
	}

	/** Adiciona as abas de inicio e conta e apresenta a dashboard. */
	protected final void finalizarDashboard(String perfil, String aviso) {
		tabs.insertTab("Inicio", null, criarInicio(perfil, aviso), null, 0);
		tabs.addTab("Conta", new UsuarioUI(perfil, conta));
		add(tabs, BorderLayout.CENTER);
		tabs.setSelectedIndex(0);
		tabs.addChangeListener(event -> atualizarSelecionado());
		tabs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "navegarComEscape");
		tabs.getActionMap().put("navegarComEscape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (tabs.getSelectedIndex() > 0) {
					tabs.setSelectedIndex(0);
				} else if (confirmarTerminoSessao()) {
					voltarMenu.run();
				}
			}
		});
	}

	/** Adiciona uma aba com tabela carregada a partir de um controlador. */
	protected final <T> void adicionarTabela(String titulo, String[] colunas, Controller<T> controller,
			Function<T, Object[]> mapear, Runnable criar) {
		tabs.addTab(titulo, criarTabelaPanel(titulo, colunas, controller, mapear, criar));
	}

	/** Regista um painel personalizado como nova aba. */
	protected final void adicionarPainel(String titulo, JPanel painel) {
		tabs.addTab(titulo, painel);
	}

	/** Atualiza periodicamente um painel apenas enquanto estiver visivel. */
	protected final void atualizarPeriodicamenteQuandoVisivel(
			JPanel painel, Runnable atualizar, int intervaloMs) {
		Timer timer = new Timer(intervaloMs, event -> {
			if (painel.isShowing()) atualizar.run();
		});
		timer.setInitialDelay(intervaloMs);
		painel.addHierarchyListener(event -> {
			if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
				if (painel.isDisplayable()) timer.start();
				else timer.stop();
			}
		});
	}

	/** Cria um modelo de tabela cujas celulas nao podem ser editadas. */
	protected final DefaultTableModel modeloTabela(String[] colunas) {
		return new DefaultTableModel(colunas, 0) {
			/** Impede a edicao direta de celulas na tabela. */
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	/** Cria um painel visual com titulo e tabela rolavel. */
	protected final JPanel criarPainelTabela(String titulo, JTable table) {
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
		JLabel heading = new JLabel(titulo);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
		heading.setForeground(PaletaCores.TEXTO);
		panel.add(heading, BorderLayout.NORTH);
		table.setRowHeight(30);
		table.setFillsViewportHeight(true);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		return panel;
	}

	/** Colore os estados de reserva para distinguir confirmacao, recusa e pendencia. */
	protected final void aplicarCoresEstadoReserva(JTable tabela, int colunaEstado) {
		tabela.getColumnModel().getColumn(colunaEstado).setCellRenderer(new DefaultTableCellRenderer() {
			/** Define as cores e o alinhamento do estado apresentado na tabela. */
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
					boolean focus, int row, int column) {
				Component componente = super.getTableCellRendererComponent(
						table, value, selected, focus, row, column);
				String estado = value == null ? "" : value.toString();
				switch (estado) {
					case "CONFIRMADA":
						componente.setBackground(new Color(209, 238, 218));
						componente.setForeground(new Color(27, 94, 52));
						break;
					case "RECUSADA":
						componente.setBackground(new Color(250, 215, 215));
						componente.setForeground(new Color(145, 35, 35));
						break;
					default:
						componente.setBackground(new Color(225, 229, 232));
						componente.setForeground(new Color(65, 73, 80));
						break;
				}
				setHorizontalAlignment(JLabel.CENTER);
				return componente;
			}
		});
	}

	/** Organiza os botoes de acao numa linha centralizada. */
	protected final JPanel criarAcoes(JButton... botoes) {
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		actions.setOpaque(false);
		for (JButton botao : botoes) {
			estilizarBotao(botao);
			actions.add(botao);
		}
		return actions;
	}

	protected final void estilizarBotao(JButton botao) {
		String texto = botao.getText().toLowerCase();
		Color fundo;
		Color textoCor;
		if (texto.contains("cancelar") || texto.contains("recusar") || texto.contains("remover")) {
			fundo = new Color(174, 60, 60);
			textoCor = Color.WHITE;
		} else if (texto.contains("atualizar")) {
			fundo = PaletaCores.SUPERFICIE;
			textoCor = PaletaCores.PRIMARIA;
		} else {
			fundo = PaletaCores.DESTAQUE;
			textoCor = Color.WHITE;
		}
		botao.setBackground(fundo);
		botao.setForeground(textoCor);
		botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
		botao.setFocusPainted(false);
		botao.setOpaque(true);
		botao.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(8, 14, 8, 14)));
		botao.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		botao.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent event) {
				if (botao.isEnabled() && !texto.contains("cancelar")
						&& !texto.contains("recusar") && !texto.contains("remover")) {
					botao.setBackground(PaletaCores.PRIMARIA);
					botao.setForeground(Color.WHITE);
				}
			}

			@Override
			public void mouseExited(MouseEvent event) {
				botao.setBackground(fundo);
				botao.setForeground(textoCor);
			}
		});
	}

	/** Mostra botoes contextuais apenas enquanto existir uma linha selecionada. */
	protected final void mostrarAcoesAoSelecionar(JTable tabela, JButton... botoes) {
		Runnable atualizarVisibilidade = () -> {
			boolean linhaSelecionada = tabela.getSelectedRow() >= 0;
			for (JButton botao : botoes) {
				botao.setVisible(linhaSelecionada);
			}
			if (botoes.length > 0 && botoes[0].getParent() != null) {
				botoes[0].getParent().revalidate();
				botoes[0].getParent().repaint();
			}
		};
		atualizarVisibilidade.run();
		tabela.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				atualizarVisibilidade.run();
			}
		});
	}

	protected final boolean mostrarFormulario(String titulo, Component formulario,
			BooleanSupplier confirmar) {
		return mostrarFormulario(titulo, formulario, confirmar, 0);
	}

	protected final boolean mostrarFormulario(String titulo, Component formulario,
			BooleanSupplier confirmar, int larguraMaxima) {
		Window proprietaria = SwingUtilities.getWindowAncestor(this);
		JDialog dialogo = new JDialog(proprietaria, titulo, Dialog.ModalityType.APPLICATION_MODAL);
		dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		formularioSubmetido = false;

		JPanel conteudo = new JPanel(new BorderLayout(0, 14));
		conteudo.setBackground(PaletaCores.FUNDO);
		conteudo.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
		conteudo.add(formulario, BorderLayout.CENTER);

		JButton guardar = new JButton("Guardar");
		JButton cancelar = new JButton("Cancelar");
		estilizarBotao(guardar);
		cancelar.setBackground(PaletaCores.SUPERFICIE);
		cancelar.setForeground(PaletaCores.SECUNDARIA);
		cancelar.setFocusPainted(false);
		cancelar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(8, 14, 8, 14)));
		JPanel acoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		acoes.setOpaque(false);
		acoes.add(guardar);
		acoes.add(cancelar);
		conteudo.add(acoes, BorderLayout.SOUTH);

		formularioAtivo = dialogo;
		guardarFormularioAtivo = guardar;
		cancelarFormularioAtivo = cancelar;
		instalarNavegacaoEnter(formulario, guardar);
		guardar.addActionListener(event -> {
			try {
				if (confirmar.getAsBoolean()) {
					guardar.setEnabled(false);
					cancelar.setEnabled(false);
				}
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(dialogo, e.getMessage(),
						"Dados invalidos", JOptionPane.WARNING_MESSAGE);
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(dialogo, "Operacao falhou: " + mensagem(e),
						"Erro", JOptionPane.ERROR_MESSAGE);
			}
		});
		cancelar.addActionListener(event -> dialogo.dispose());
		dialogo.setContentPane(conteudo);
		dialogo.getRootPane().setDefaultButton(guardar);
		dialogo.pack();
		if (larguraMaxima > 0 && dialogo.getWidth() > larguraMaxima) {
			dialogo.setSize(larguraMaxima, dialogo.getHeight());
		}
		dialogo.setLocationRelativeTo(this);
		dialogo.setVisible(true);
		boolean submetido = formularioSubmetido;
		if (formularioAtivo == dialogo) {
			formularioAtivo = null;
			guardarFormularioAtivo = null;
			cancelarFormularioAtivo = null;
		}
		return submetido;
	}

	protected final void fecharFormularioAtual() {
		if (formularioAtivo == null) return;
		JDialog dialogo = formularioAtivo;
		formularioSubmetido = true;
		formularioAtivo = null;
		guardarFormularioAtivo = null;
		cancelarFormularioAtivo = null;
		dialogo.dispose();
	}

	protected final void restaurarFormularioAtual() {
		if (guardarFormularioAtivo != null) guardarFormularioAtivo.setEnabled(true);
		if (cancelarFormularioAtivo != null) cancelarFormularioAtivo.setEnabled(true);
	}

	/** Mostra ou oculta a senha sem alterar o valor introduzido. */
	protected static JPanel campoSenha(JPasswordField campo) {
		JPanel painel = new JPanel(new BorderLayout(6, 0));
		painel.setOpaque(false);
		JButton mostrar = new JButton("Ver");
		char caractereOriginal = campo.getEchoChar();
		mostrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
		mostrar.setForeground(PaletaCores.PRIMARIA);
		mostrar.setBackground(PaletaCores.SUPERFICIE);
		mostrar.setOpaque(true);
		mostrar.setFocusPainted(false);
		mostrar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		mostrar.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		mostrar.setToolTipText("Mostrar senha");
		mostrar.setFocusable(false);
		mostrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent event) {
				mostrar.setBackground(new Color(235, 245, 245));
			}

			@Override
			public void mouseExited(MouseEvent event) {
				mostrar.setBackground(PaletaCores.SUPERFICIE);
			}
		});
		mostrar.addActionListener(event -> {
			boolean visivel = campo.getEchoChar() != 0;
			campo.setEchoChar(visivel ? (char) 0 : caractereOriginal);
			mostrar.setText(visivel ? "Ocultar" : "Ver");
			mostrar.setToolTipText(visivel ? "Ocultar senha" : "Mostrar senha");
		});
		painel.add(campo, BorderLayout.CENTER);
		painel.add(mostrar, BorderLayout.EAST);
		return painel;
	}

	/** Faz Enter avancar pelos campos e submeter o formulario no ultimo campo. */
	protected static void instalarNavegacaoEnter(Component formulario, JButton acaoFinal) {
		List<Component> campos = new java.util.ArrayList<>();
		recolherCampos(formulario, campos);
		for (int i = 0; i < campos.size(); i++) {
			Component campo = campos.get(i);
			final Component proximo = i + 1 < campos.size() ? campos.get(i + 1) : null;
			campo.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent event) {
					if (event.getKeyCode() != KeyEvent.VK_ENTER
							|| campo instanceof JComboBox<?> combo && combo.isPopupVisible()) {
						return;
					}
					event.consume();
					if (proximo != null) {
						proximo.requestFocusInWindow();
					} else if (acaoFinal.isEnabled()) {
						acaoFinal.doClick();
					}
				}
			});
		}
	}

	private static void recolherCampos(Component componente, List<Component> campos) {
		if (componente.isEnabled() && componente.isFocusable()
				&& (componente instanceof JTextField
						|| componente instanceof JComboBox<?>
						|| componente instanceof JList<?>)) {
			campos.add(componente);
		}
		if (componente instanceof Container contentor) {
			for (Component filho : contentor.getComponents()) {
				recolherCampos(filho, campos);
			}
		}
	}

	/** Cria uma caixa de selecao tipada a partir de uma lista de objetos. */
	protected final <T> JComboBox<Opcao<T>> comboOpcoes(List<T> itens, Function<T, String> descricao) {
		JComboBox<Opcao<T>> combo = new JComboBox<>();
		for (T item : itens) {
			combo.addItem(new Opcao<>(item, descricao.apply(item)));
		}
		return combo;
	}

	/** Obtem o objeto associado a opcao selecionada na caixa. */
	protected final <T> T valorSelecionado(JComboBox<Opcao<T>> combo) {
		int indiceSelecionado = combo.getSelectedIndex();
		Opcao<T> selecionada = indiceSelecionado < 0 ? null : combo.getItemAt(indiceSelecionado);
		return valorOpcao(selecionada);
	}

	/** Extrai o valor guardado numa opcao, ou null se nao houver opcao. */
	protected final <T> T valorOpcao(Opcao<T> opcao) {
		return opcao == null ? null : opcao.valor;
	}

	/** Apresenta uma mensagem de erro ao utilizador. */
	protected final void mostrarErro(String texto) {
		JOptionPane.showMessageDialog(janelaAtiva(), texto, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	protected final void mostrarInformacao(String texto) {
		JOptionPane.showMessageDialog(janelaAtiva(), texto,
				"Informacao", JOptionPane.INFORMATION_MESSAGE);
	}

	private Window janelaAtiva() {
		Window ativa = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		return ativa == null ? SwingUtilities.getWindowAncestor(this) : ativa;
	}

	/** Executa uma operacao com indicacao de progresso e tratamento de erros. */
	protected final boolean executarOperacao(String titulo, Runnable operacao, String sucesso) {
		return executarOperacao(titulo, operacao, sucesso, () -> { });
	}

	protected final boolean executarOperacao(String titulo, Runnable operacao, String sucesso,
			Runnable aoFalhar) {
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		return DatabaseTaskExecutor.submit(operacao, () -> {
			fecharFormularioAtual();
			setCursor(java.awt.Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(janelaAtiva(), sucesso, titulo, JOptionPane.INFORMATION_MESSAGE);
			atualizarSelecionado();
		}, exception -> {
			restaurarFormularioAtual();
			setCursor(java.awt.Cursor.getDefaultCursor());
			mostrarErro("Operacao falhou: " + mensagem(exception));
			aoFalhar.run();
		});
	}

	/** Obtem uma mensagem util da excecao ou da sua causa. */
	protected final String mensagem(Exception exception) {
		Throwable causa = exception.getCause() != null ? exception.getCause() : exception;
		return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
	}

	/** Cria a aba inicial com saudacao e aviso opcional. */
	private JPanel criarInicio(String perfil, String aviso) {
		JPanel panel = new JPanel(new BorderLayout(0, 16));
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
		JLabel greetingLabel = new JLabel("Bem-vindo, " + email);
		greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		greetingLabel.setForeground(PaletaCores.TEXTO);
		JLabel description = new JLabel("Aceda pelo menu lateral as ferramentas disponiveis para "
				+ nomePerfil(perfil).toLowerCase() + ".");
		description.setForeground(PaletaCores.SECUNDARIA);
		JPanel intro = new JPanel(new BorderLayout(0, 10));
		intro.setOpaque(false);
		intro.add(greetingLabel, BorderLayout.NORTH);
		intro.add(description, BorderLayout.CENTER);
		if (aviso != null) {
			JLabel notice = new JLabel(aviso);
			notice.setForeground(PaletaCores.AVISO);
			intro.add(notice, BorderLayout.SOUTH);
		}
		panel.add(intro, BorderLayout.NORTH);
		return panel;
	}

	/** Cria o cabecalho com a conta e o comando para terminar sessao. */
	private JPanel criarCabecalho(String perfil) {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(PaletaCores.PRIMARIA);
		header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
		JLabel title = new JLabel("Gestao de salas");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(PaletaCores.SUPERFICIE);
		accountLabel = new JLabel(nomePerfil(perfil) + "  |  " + email);
		accountLabel.setForeground(PaletaCores.TEXTO_CLARO);
		JPanel accountPanel = new JPanel(new GridBagLayout());
		accountPanel.setOpaque(false);
		accountPanel.add(accountLabel);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.insets = new Insets(0, 14, 0, 0);
		JButton logout = new JButton("Terminar sessao");
		estilizarBotao(logout);
		logout.addActionListener(event -> {
			if (confirmarTerminoSessao()) {
				voltarMenu.run();
			}
		});
		accountPanel.add(logout, constraints);
		header.add(title, BorderLayout.WEST);
		header.add(accountPanel, BorderLayout.EAST);
		return header;
	}

	/** Solicita confirmacao da saida e aceita Enter para confirmar e Escape para cancelar. */
	private boolean confirmarTerminoSessao() {
		JButton sim = new JButton("Sim");
		JButton nao = new JButton("Nao");
		estilizarBotao(sim);
		nao.setBackground(PaletaCores.SUPERFICIE);
		nao.setForeground(PaletaCores.SECUNDARIA);
		nao.setFocusPainted(false);
		nao.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(8, 14, 8, 14)));
		JOptionPane escolha = new JOptionPane("Tem a certeza de que deseja terminar a sessao?",
				JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				new Object[]{sim, nao}, sim);
		JDialog dialogo = escolha.createDialog(this, "Confirmar saida");
		sim.addActionListener(event -> {
			escolha.setValue(JOptionPane.YES_OPTION);
			dialogo.dispose();
		});
		nao.addActionListener(event -> {
			escolha.setValue(JOptionPane.NO_OPTION);
			dialogo.dispose();
		});
		dialogo.getRootPane().setDefaultButton(sim);
		dialogo.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "naoTerminarSessao");
		dialogo.getRootPane().getActionMap().put("naoTerminarSessao", new AbstractAction() {
			@Override
			/** Fecha o dialogo sem terminar a sessao quando Escape e pressionado. */
			public void actionPerformed(ActionEvent event) {
				escolha.setValue(JOptionPane.NO_OPTION);
				dialogo.dispose();
			}
		});
		dialogo.setVisible(true);
		return Integer.valueOf(JOptionPane.YES_OPTION).equals(escolha.getValue());
	}

	/** Constroi uma aba com acoes para atualizar e, opcionalmente, criar registos. */
	private <T> JPanel criarTabelaPanel(String titulo, String[] colunas, Controller<T> controller,
			Function<T, Object[]> mapear, Runnable criar) {
		DefaultTableModel model = modeloTabela(colunas);
		JPanel panel = criarPainelTabela(titulo, new JTable(model));
		JButton refresh = new JButton("Atualizar");
		JButton add = criar == null ? null : new JButton("Adicionar");
		JPanel actions = criarAcoes(add == null ? new JButton[]{refresh} : new JButton[]{refresh, add});
		panel.add(actions, BorderLayout.SOUTH);
		Runnable reload = () -> carregar(controller, mapear, model, refresh);
		refresh.addActionListener(event -> reload.run());
		if (add != null) {
			add.addActionListener(event -> criar.run());
		}
		panel.putClientProperty("reload", reload);
		return panel;
	}

	/** Carrega os dados do controlador e preenche o modelo da tabela. */
	private <T> void carregar(Controller<T> controller, Function<T, Object[]> mapear,
			DefaultTableModel model, JButton refresh) {
		executarAtualizacao(refresh, controller::listar, items -> {
			model.setRowCount(0);
			for (T item : items) {
				model.addRow(mapear.apply(item));
			}
		});
	}

	protected final <T> void executarAtualizacao(JButton atualizar, Supplier<T> carregar,
			Consumer<T> aplicar) {
		if (!atualizar.isEnabled()) return;
		atualizar.setEnabled(false);
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		DatabaseTaskExecutor.submit(carregar, resultado -> {
			try {
				aplicar.accept(resultado);
			} finally {
				atualizar.setEnabled(true);
				setCursor(java.awt.Cursor.getDefaultCursor());
			}
		}, exception -> {
			mostrarErro("Nao foi possivel carregar os dados: " + mensagem(exception));
			atualizar.setEnabled(true);
			setCursor(java.awt.Cursor.getDefaultCursor());
		});
	}

	/** Executa a atualizacao da aba selecionada quando existe uma acao de recarga. */
	protected final void atualizarSelecionado() {
		if (tabs.getSelectedComponent() instanceof JPanel panel) {
			Object reload = panel.getClientProperty("reload");
			if (reload instanceof Runnable action) {
				action.run();
			}
		}
	}

	/** Converte o codigo do perfil para um nome apresentado na interface. */
	private static String nomePerfil(String codigo) {
		switch (codigo.toUpperCase()) {
			case "ADMIN": return "Administrador";
			case "SECRETARIO": return "Secretario";
			case "DOCENTE": return "Docente";
			case "ESTUDANTE": return "Estudante";
			default: return codigo;
		}
	}

	protected static final class Opcao<T> {
		private final T valor;
		private final String descricao;

		/** Guarda o valor e o texto visivel de uma opcao de selecao. */
		protected Opcao(T valor, String descricao) {
			this.valor = valor;
			this.descricao = descricao;
		}

		@Override
		/** Devolve o texto apresentado pela opcao nas caixas de selecao. */
		public String toString() {
			return descricao;
		}
	}

}
