package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.ConexaoSql;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MenuUI extends JFrame {
	private static final Color INK = new Color(31, 49, 45);
	private static final Color GREEN = new Color(38, 105, 78);
	private static final Color PAPER = new Color(246, 247, 241);
	private final AutenticacaoDAO autenticacaoDAO = new AutenticacaoDAO();
	private final JPanel content = new JPanel(new BorderLayout());
	private String perfil;

	public MenuUI() {
		super("SGSAS | Gestão de salas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(760, 560));
		setLocationRelativeTo(null);
		content.setBackground(PAPER);
		setContentPane(content);
		mostrarPerfis();
	}

	private void mostrarPerfis() {
		JPanel page = new JPanel(new BorderLayout(0, 24));
		page.setBackground(PAPER);
		page.setBorder(BorderFactory.createEmptyBorder(38, 48, 30, 48));

		JLabel brand = new JLabel("SGSAS  /  PORTAL ACADEMICO");
		brand.setFont(new Font("Segoe UI", Font.BOLD, 12));
		brand.setForeground(GREEN);
		JLabel heading = new JLabel("Como pretende aceder?");
		heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
		heading.setForeground(INK);
		JLabel subtitle = new JLabel("Escolha o seu perfil para continuar para o inicio de sessao.");
		subtitle.setForeground(new Color(92, 105, 97));
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
		footer.setForeground(new Color(112, 121, 113));
		page.add(footer, BorderLayout.SOUTH);
		mostrar(page);
	}

	private void adicionarPerfil(JPanel cards, String nome, String descricao, String codigo) {
		JButton card = new JButton("<html><div style='font-size:18px;font-weight:bold'>" + nome
				+ "</div><div style='font-size:12px;margin-top:7px'>" + descricao
				+ "</div><div style='font-size:12px;margin-top:14px'>Continuar &rarr;</div></html>");
		card.setHorizontalAlignment(JButton.LEFT);
		card.setVerticalAlignment(JButton.CENTER);
		card.setForeground(INK);
		card.setBackground(Color.WHITE);
		card.setFocusPainted(false);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(222, 225, 216)),
				BorderFactory.createEmptyBorder(18, 20, 18, 20)));
		card.addActionListener(event -> {
			perfil = codigo;
			mostrarLogin();
		});
		cards.add(card);
	}

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
			String endereco = email.getText().trim();
			String password = new String(senha.getPassword());
			if (endereco.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Preencha o e-mail e a senha.", "Dados em falta",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			entrar.setEnabled(false);
			try {
				if (autenticacaoDAO.autenticarLogin(perfil, endereco, password)) {
					abrirPainel(perfil, endereco);
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
			String endereco = email.getText().trim();
			String password = new String(senha.getPassword());
			if (!endereco.contains("@") || password.length() < 8
					|| !password.equals(new String(confirmar.getPassword()))) {
				JOptionPane.showMessageDialog(this, "Informe um e-mail válido e senhas iguais com pelo menos 8 caracteres.",
						"Dados inválidos", JOptionPane.WARNING_MESSAGE);
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

	private JPanel criarFormulario(String titulo, String subtitulo) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(246, 247, 241));
		panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 8, 0);
		JLabel heading = new JLabel(titulo);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
		heading.setForeground(INK);
		panel.add(heading, constraints);
		constraints.gridy++;
		constraints.insets = new Insets(0, 0, 24, 0);
		JLabel caption = new JLabel(subtitulo);
		caption.setForeground(new Color(92, 105, 97));
		panel.add(caption, constraints);
		return panel;
	}

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

	private JButton botao(String texto) {
		JButton button = new JButton(texto);
		button.setBackground(GREEN);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		return button;
	}

	private void mostrar(JPanel panel) {
		content.removeAll();
		content.add(panel, BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
	}

	private void abrirPainel(String perfil, String email) {
		AdminUI painel = new AdminUI(perfil, email);
		painel.setVisible(true);
		dispose();
	}

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

	private void mostrarErro(String texto) {
		JOptionPane.showMessageDialog(this, texto, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	private String mensagem(Exception exception) {
		Throwable causa = exception.getCause() != null ? exception.getCause() : exception;
		return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
	}
}