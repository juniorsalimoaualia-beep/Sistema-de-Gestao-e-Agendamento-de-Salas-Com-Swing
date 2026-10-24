package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

public class UsuarioUI extends JPanel {
	private final JPasswordField passwordField = new JPasswordField(20);
	private final JPasswordField confirmPasswordField = new JPasswordField(20);
	private char[] senhaDemo = "demo123".toCharArray();

	/** Monta os dados da conta e a area para alterar a senha. */
	public UsuarioUI(String perfil, int id, String nomeCompleto, int celular, String email) {
		super(new BorderLayout(0, 18));
		setBackground(PaletaCores.FUNDO);
		setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

		JLabel title = new JLabel("Conta do " + perfil.toLowerCase());
		title.setFont(new Font("Segoe UI", Font.BOLD, 20));
		title.setForeground(PaletaCores.TEXTO);

		JPanel details = new JPanel(new GridLayout(0, 2, 16, 14));
		details.setBackground(PaletaCores.SUPERFICIE);
		details.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(20, 22, 20, 22)));
		adicionarDetalhe(details, "Nome completo", nomeCompleto);
		adicionarDetalhe(details, "ID", Integer.toString(id));
		adicionarDetalhe(details, "Numero de celular", Integer.toString(celular));
		adicionarDetalhe(details, "E-mail", email);

		JButton changePassword = new JButton("Alterar senha");
		changePassword.setBackground(PaletaCores.PRIMARIA);
		changePassword.setForeground(PaletaCores.SUPERFICIE);
		changePassword.setFocusPainted(false);
		changePassword.setPreferredSize(new java.awt.Dimension(150, 36));
		JPanel passwordForm = new JPanel(new GridLayout(2, 2, 10, 8));
		passwordForm.setBackground(PaletaCores.SUPERFICIE);
		passwordForm.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(18, 22, 18, 22)));
		passwordForm.add(rotulo("Nova senha"));
		passwordForm.add(passwordField);
		passwordForm.add(rotulo("Confirmar senha"));
		passwordForm.add(confirmPasswordField);

		JPanel passwordSection = new JPanel(new BorderLayout(0, 12));
		passwordSection.setOpaque(false);
		JLabel passwordTitle = new JLabel("Seguranca");
		passwordTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		passwordTitle.setForeground(PaletaCores.TEXTO);
		JPanel passwordActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		passwordActions.setOpaque(false);
		passwordActions.add(changePassword);
		passwordSection.add(passwordTitle, BorderLayout.NORTH);
		passwordSection.add(passwordForm, BorderLayout.CENTER);
		passwordSection.add(passwordActions, BorderLayout.SOUTH);

		JPanel accountContent = new JPanel(new BorderLayout(0, 22));
		accountContent.setOpaque(false);
		accountContent.add(details, BorderLayout.NORTH);
		accountContent.add(passwordSection, BorderLayout.CENTER);
		JLabel notice = new JLabel("Modo demonstracao: alteracoes validas apenas nesta sessao.");
		notice.setForeground(PaletaCores.AVISO);
		add(title, BorderLayout.NORTH);
		JScrollPane accountScroll = new JScrollPane(accountContent);
		accountScroll.setBorder(BorderFactory.createEmptyBorder());
		accountScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		accountScroll.getVerticalScrollBar().setUnitIncrement(16);
		add(accountScroll, BorderLayout.CENTER);
		add(notice, BorderLayout.SOUTH);

		changePassword.addActionListener(event -> alterarSenha());
	}

	/** Adiciona ao painel um par de rotulo e valor da conta. */
	private void adicionarDetalhe(JPanel panel, String label, String valor) {
		adicionarCampo(panel, label, valor);
	}

	/** Cria e insere o rotulo e o valor de um campo da conta. */
	private void adicionarCampo(JPanel panel, String label, String valor) {
		JLabel heading = new JLabel(label);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 12));
		heading.setForeground(PaletaCores.SECUNDARIA);
		JLabel value = new JLabel(valor);
		value.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		value.setForeground(PaletaCores.TEXTO);
		panel.add(heading);
		panel.add(value);
	}

	/** Cria um rotulo secundario para o formulario de senha. */
	private JLabel rotulo(String texto) {
		JLabel label = new JLabel(texto);
		label.setForeground(PaletaCores.SECUNDARIA);
		return label;
	}

	/** Valida a nova senha e atualiza a senha demonstrativa da sessao. */
	private void alterarSenha() {
		char[] novaSenha = passwordField.getPassword();
		char[] confirmacao = confirmPasswordField.getPassword();
		try {
			if (novaSenha.length < 8) {
				JOptionPane.showMessageDialog(this, "A nova senha deve ter pelo menos 8 caracteres.",
						"Senha invalida", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!java.util.Arrays.equals(novaSenha, confirmacao)) {
				JOptionPane.showMessageDialog(this, "A confirmacao da senha nao corresponde.",
						"Senhas diferentes", JOptionPane.WARNING_MESSAGE);
				return;
			}
			java.util.Arrays.fill(senhaDemo, '\0');
			senhaDemo = novaSenha.clone();
			passwordField.setText("");
			confirmPasswordField.setText("");
			JOptionPane.showMessageDialog(this, "Senha alterada nesta sessao.");
		} finally {
			java.util.Arrays.fill(novaSenha, '\0');
			java.util.Arrays.fill(confirmacao, '\0');
		}
	}
}
