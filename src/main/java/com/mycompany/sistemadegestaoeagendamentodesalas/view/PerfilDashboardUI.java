package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
	private final String nomeCompleto;
	private final int idConta;
	private final int celular;

	/** Inicializa a estrutura comum dos paineis por perfil. */
	PerfilDashboardUI(String perfil, String email, int utilizadorId, Runnable voltarMenu) {
		super(new BorderLayout());
		this.email = email;
		this.utilizadorId = utilizadorId;
		this.voltarMenu = voltarMenu;
		this.perfil = perfil;
		nomeCompleto = nomeDemo(perfil);
		idConta = idDemo(perfil);
		celular = celularDemo(perfil);
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
		tabs.addTab("Conta", new UsuarioUI(perfil, idConta, nomeCompleto, celular, email));
		add(tabs, BorderLayout.CENTER);
		tabs.setSelectedIndex(0);
		tabs.addChangeListener(event -> atualizarSelecionado());
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

	/** Organiza os botoes de acao numa linha alinhada a esquerda. */
	protected final JPanel criarAcoes(JButton... botoes) {
		JPanel actions = new JPanel(new GridBagLayout());
		actions.setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 8);
		constraints.anchor = GridBagConstraints.WEST;
		for (JButton botao : botoes) {
			actions.add(botao, constraints);
			constraints.gridx++;
		}
		return actions;
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
		JOptionPane.showMessageDialog(this, texto, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	/** Executa uma operacao com indicacao de progresso e tratamento de erros. */
	protected final void executarOperacao(String titulo, Runnable operacao, String sucesso) {
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		try {
			operacao.run();
			JOptionPane.showMessageDialog(this, sucesso, titulo, JOptionPane.INFORMATION_MESSAGE);
			atualizarSelecionado();
		} catch (RuntimeException e) {
			mostrarErro("Operacao falhou: " + mensagem(e));
		} finally {
			setCursor(java.awt.Cursor.getDefaultCursor());
		}
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
		logout.setBackground(PaletaCores.DESTAQUE);
		logout.setForeground(PaletaCores.SUPERFICIE);
		logout.setFocusPainted(false);
		Color corSaida = PaletaCores.DESTAQUE;
		logout.addMouseListener(new MouseAdapter() {
			@Override
			/** Realca o botao quando o cursor entra na area. */
			public void mouseEntered(MouseEvent event) {
				logout.setBackground(PaletaCores.PRIMARIA);
			}

			@Override
			/** Repoe a cor do botao quando o cursor sai da area. */
			public void mouseExited(MouseEvent event) {
				logout.setBackground(corSaida);
			}
		});
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
		refresh.setEnabled(false);
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		try {
			List<T> items = controller.listar();
			model.setRowCount(0);
			for (T item : items) {
				model.addRow(mapear.apply(item));
			}
		} catch (RuntimeException e) {
			mostrarErro("Nao foi possivel carregar os dados: " + mensagem(e));
		} finally {
			refresh.setEnabled(true);
			setCursor(java.awt.Cursor.getDefaultCursor());
		}
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

	/** Obtem o nome de demonstracao associado a cada perfil. */
	private static String nomeDemo(String perfil) {
		switch (perfil.toUpperCase()) {
			case "ADMIN": return "Administrador de Demonstracao";
			case "SECRETARIO": return "Celina Mavume";
			case "DOCENTE": return "Ana Mucavele";
			case "ESTUDANTE": return "Carlos Nhantumbo";
			default: return "Utilizador";
		}
	}

	/** Obtem o identificador demonstrativo associado a cada perfil. */
	private static int idDemo(String perfil) {
		switch (perfil.toUpperCase()) {
			case "ADMIN": return 1;
			case "SECRETARIO": return 2;
			case "DOCENTE": return 1;
			case "ESTUDANTE": return 101;
			default: return 0;
		}
	}

	/** Obtem o numero de telefone demonstrativo associado a cada perfil. */
	private static int celularDemo(String perfil) {
		switch (perfil.toUpperCase()) {
			case "ADMIN": return 840000001;
			case "SECRETARIO": return 840000002;
			case "DOCENTE": return 840000003;
			case "ESTUDANTE": return 840000004;
			default: return 0;
		}
	}
}
