package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MenuUI extends JFrame {
	private final AutenticacaoDAO autenticacaoDAO = new AutenticacaoDAO();
	private final JPanel content = new JPanel(new BorderLayout());
	private String perfil;

	/** Configura a janela principal e apresenta a escolha de perfil. */
	public MenuUI() {
		super("SGSAS | Gestão de salas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(760, 560));
		setLocationRelativeTo(null);
		content.setBackground(PaletaCores.FUNDO);
		setContentPane(content);
		mostrarPerfis();
	}

	/** Mostra os cartoes para escolher o perfil de acesso. */
	private void mostrarPerfis() {
		setTitle("SGSAS | Gestão de salas");
		JPanel page = new JPanel(new BorderLayout(0, 24));
		page.setBackground(PaletaCores.FUNDO);
		page.setBorder(BorderFactory.createEmptyBorder(38, 48, 30, 48));

		JLabel brand = new JLabel("SGSAS  /  PORTAL ACADEMICO");
		brand.setFont(new Font("Segoe UI", Font.BOLD, 12));
		brand.setForeground(PaletaCores.DESTAQUE);
		JLabel heading = new JLabel("Como pretende aceder?");
		heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
		heading.setForeground(PaletaCores.TEXTO);
		JLabel subtitle = new JLabel("Escolha um perfil. Depois inicie sessao para abrir a dashboard.");
		subtitle.setForeground(PaletaCores.SECUNDARIA);
		JPanel titles = new JPanel(new GridLayout(3, 1, 0, 7));
		titles.setOpaque(false);
		titles.add(brand);
		titles.add(heading);
		titles.add(subtitle);
		page.add(titles, BorderLayout.NORTH);

		JPanel cards = new JPanel(new GridLayout(2, 2, 14, 14));
		cards.setOpaque(false);
		adicionarPerfil(cards, "Administrador", "Gestao e configuracao do sistema", "ADMIN");
		adicionarPerfil(cards, "Secretario", "Salas, reservas e horarios", "SECRETARIO");
		adicionarPerfil(cards, "Docente", "Disciplinas e reservas de espacos", "DOCENTE");
		adicionarPerfil(cards, "Estudante", "Horario, disciplinas e inscricoes", "ESTUDANTE");
		page.add(cards, BorderLayout.CENTER);

		JLabel footer = new JLabel("Acesso seguro  /  Sistema de gestao e agendamento de salas");
		footer.setForeground(PaletaCores.SECUNDARIA);
		page.add(footer, BorderLayout.SOUTH);
		mostrar(page);
	}

	/** Cria um cartao de perfil e liga-o ao painel correspondente. */
	private void adicionarPerfil(JPanel cards, String nome, String descricao, String codigo) {
		JButton card = new JButton();
		card.setLayout(new BorderLayout(0, 10));
		card.setHorizontalAlignment(JButton.LEFT);
		card.setBackground(PaletaCores.SUPERFICIE);
		card.setFocusPainted(false);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(18, 20, 18, 20)));
		JLabel title = new JLabel(nome);
		title.setFont(new Font("Segoe UI", Font.BOLD, 18));
		title.setForeground(PaletaCores.TEXTO);
		JLabel detail = new JLabel(descricao);
		detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		detail.setForeground(PaletaCores.SECUNDARIA);
		JPanel info = new JPanel(new GridLayout(2, 1, 0, 7));
		info.setOpaque(false);
		info.add(title);
		info.add(detail);
		card.add(info, BorderLayout.CENTER);
		card.addMouseListener(new MouseAdapter() {
			/** Realca o cartao quando o cursor entra na area. */
			@Override
			public void mouseEntered(MouseEvent event) {
				card.setBackground(new java.awt.Color(235, 245, 245));
			}

			/** Repoe a cor normal quando o cursor sai do cartao. */
			@Override
			public void mouseExited(MouseEvent event) {
				card.setBackground(PaletaCores.SUPERFICIE);
			}
		});
		card.addActionListener(event -> {
			perfil = codigo;
			mostrarLogin();
		});
		cards.add(card);
	}

	/** Apresenta e valida o formulario de autenticacao. */
	private void mostrarLogin() {
		JTextField email = new JTextField(24);
		JPasswordField senha = new JPasswordField(24);
		JPanel form = criarFormulario("Iniciar sessao", "Acesso como " + nomePerfil(perfil));
		adicionarCampo(form, 0, "E-mail", email);
		adicionarCampo(form, 1, "Senha", senha);
		JButton entrar = botao("Entrar");
		JButton voltar = new JButton("Voltar aos perfis");
		JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
		actions.setOpaque(false);
		actions.add(entrar);
		actions.add(voltar);
		adicionarCampo(form, 2, "", actions);
		if ("ADMIN".equals(perfil)) {
			JButton criarAdmin = new JButton("Criar o primeiro administrador");
			adicionarCampo(form, 3, "", criarAdmin);
			criarAdmin.addActionListener(event -> mostrarRegistoAdministrador());
		}
		entrar.addActionListener(event -> {
			String password = new String(senha.getPassword());
			if (password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Preencha o e-mail e a senha.", "Dados em falta",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			String endereco;
			try {
				endereco = Validacao.validarEnderecoEmail(email.getText());
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "E-mail invalido",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			entrar.setEnabled(false);
			try {
				if (autenticacaoDAO.autenticarLogin(perfil, endereco, password)) {
					abrirPainel(perfil, endereco, autenticacaoDAO.buscarIdUtilizador(perfil, endereco));
				} else {
					JOptionPane.showMessageDialog(this, "Credenciais invalidas para o perfil selecionado.",
							"Acesso negado", JOptionPane.WARNING_MESSAGE);
				}
			} catch (RuntimeException e) {
				mostrarErro("Falha ao autenticar: " + mensagem(e));
			} finally {
				entrar.setEnabled(true);
			}
		});
		voltar.addActionListener(event -> mostrarPerfis());
		mostrar(form);
	}

	/** Apresenta o formulario para criar a conta inicial de administrador. */
	private void mostrarRegistoAdministrador() {
		JTextField email = new JTextField(24);
		JPasswordField senha = new JPasswordField(24);
		JPasswordField confirmar = new JPasswordField(24);
		JPanel form = criarFormulario("Primeiro acesso", "Registe a conta inicial de administrador");
		adicionarCampo(form, 0, "E-mail", email);
		adicionarCampo(form, 1, "Senha", senha);
		adicionarCampo(form, 2, "Confirmar senha", confirmar);
		JButton criar = botao("Criar administrador");
		JButton voltar = new JButton("Voltar ao login");
		JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
		actions.setOpaque(false);
		actions.add(criar);
		actions.add(voltar);
		adicionarCampo(form, 3, "", actions);
		criar.addActionListener(event -> {
			String endereco;
			String password = new String(senha.getPassword());
			try {
				endereco = Validacao.validarEnderecoEmail(email.getText());
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "E-mail invalido",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (password.length() < 8 || !password.equals(new String(confirmar.getPassword()))) {
				JOptionPane.showMessageDialog(this,
						"As senhas devem coincidir e ter pelo menos 8 caracteres.",
						"Senha invalida", JOptionPane.WARNING_MESSAGE);
				return;
			}
			criar.setEnabled(false);
			try {
				if (autenticacaoDAO.existeAdministrador()) {
					JOptionPane.showMessageDialog(this, "Ja existe um administrador. Inicie sessao com essa conta.",
							"Registo indisponivel", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				autenticacaoDAO.criarAdministrador(endereco, password);
				JOptionPane.showMessageDialog(this, "Administrador registado. Inicie sessao para continuar.");
				mostrarLogin();
			} catch (RuntimeException e) {
				mostrarErro("Nao foi possivel criar a conta: " + mensagem(e));
			} finally {
				criar.setEnabled(true);
			}
		});
		voltar.addActionListener(event -> mostrarLogin());
		mostrar(form);
	}

	/** Cria um painel de formulario com titulo e descricao. */
	private JPanel criarFormulario(String titulo, String subtitulo) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 8, 0);
		JLabel heading = new JLabel(titulo);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
		heading.setForeground(PaletaCores.TEXTO);
		panel.add(heading, constraints);
		constraints.gridy++;
		constraints.insets = new Insets(0, 0, 24, 0);
		JLabel caption = new JLabel(subtitulo);
		caption.setForeground(PaletaCores.SECUNDARIA);
		panel.add(caption, constraints);
		return panel;
	}

	/** Adiciona um rotulo e um campo ao formulario alinhado. */
	private void adicionarCampo(JPanel panel, int row, String label, java.awt.Component input) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = row + 2;
		constraints.insets = new Insets(7, 0, 7, 10);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		panel.add(new JLabel(label), constraints);
		constraints.gridx = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		input.setPreferredSize(new Dimension(250, 34));
		panel.add(input, constraints);
	}

	/** Cria um botao principal com o estilo da aplicacao. */
	private JButton botao(String texto) {
		JButton button = new JButton(texto);
		button.setBackground(PaletaCores.PRIMARIA);
		button.setForeground(PaletaCores.SUPERFICIE);
		button.setFocusPainted(false);
		return button;
	}

	/** Substitui o conteudo visivel da janela pela pagina indicada. */
	private void mostrar(JPanel panel) {
		content.removeAll();
		content.add(panel, BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
	}

	/** Cria e apresenta o painel correspondente ao perfil selecionado. */
	private void abrirPainel(String perfil, String email, int utilizadorId) {
		try {
			PerfilDashboardUI painel;
			switch (perfil.toUpperCase()) {
				case "ADMIN":
					painel = new AdminUI(perfil, email, utilizadorId, this::mostrarPerfis);
					break;
				case "SECRETARIO":
					painel = new SecretarioUI(email, utilizadorId, this::mostrarPerfis);
					break;
				case "DOCENTE":
					painel = new DocenteUI(email, utilizadorId, this::mostrarPerfis);
					break;
				case "ESTUDANTE":
					painel = new EstudanteUI(email, utilizadorId, this::mostrarPerfis);
					break;
				default:
					throw new IllegalArgumentException("Perfil desconhecido: " + perfil);
			}
			setTitle("SGSAS | Painel de " + nomePerfil(perfil));
			mostrar(painel);
		} catch (RuntimeException e) {
			mostrarErro("Nao foi possivel abrir o painel de " + nomePerfil(perfil) + ": " + mensagem(e));
		}
	}

	/** Devolve o nome legivel associado ao codigo do perfil. */
	private String nomePerfil(String codigo) {
		switch (codigo) {
			case "ADMIN":
				return "Administrador";
			case "SECRETARIO":
				return "Secretario";
			case "DOCENTE":
				return "Docente";
			case "ESTUDANTE":
				return "Estudante";
			default:
				return codigo;
		}
	}

	/** Apresenta uma mensagem de erro ao utilizador. */
	private void mostrarErro(String texto) {
		JOptionPane.showMessageDialog(this, texto, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	/** Extrai uma mensagem adequada da excecao recebida. */
	private String mensagem(Exception exception) {
		Throwable causa = exception.getCause() != null ? exception.getCause() : exception;
		return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
	}
}