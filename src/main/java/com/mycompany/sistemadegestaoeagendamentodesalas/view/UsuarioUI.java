package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Usuario;

public class UsuarioUI extends JPanel {
	private final int usuarioId;
	private final JPasswordField currentPasswordField = new JPasswordField(20);
	private final JPasswordField passwordField = new JPasswordField(20);
	private final JPasswordField confirmPasswordField = new JPasswordField(20);
	private final AutenticacaoDAO autenticacaoDAO = new AutenticacaoDAO();

	/** Carrega os dados da conta autenticada e prepara a alteracao de senha. */
	public UsuarioUI(String perfil, Usuario usuario) {
		super(new BorderLayout(0, 18));
		usuarioId = usuario.getId();
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
		adicionarDetalhe(details, "Nome completo", usuario.getNomeCompleto());
		adicionarDetalhe(details, "ID", Integer.toString(usuario.getId()));
		adicionarDetalhe(details, "Numero de celular", Integer.toString(usuario.getNumCel()));
		adicionarDetalhe(details, "E-mail", usuario.getEmail());

		JButton changePassword = new JButton("Alterar senha");
		changePassword.setBackground(PaletaCores.PRIMARIA);
		changePassword.setForeground(PaletaCores.SUPERFICIE);
		changePassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
		changePassword.setFocusPainted(false);
		changePassword.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(9, 15, 9, 15)));
		changePassword.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		changePassword.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent event) {
				changePassword.setBackground(PaletaCores.DESTAQUE);
			}

			@Override
			public void mouseExited(MouseEvent event) {
				changePassword.setBackground(PaletaCores.PRIMARIA);
			}
		});
		JPanel passwordForm = new JPanel(new GridLayout(3, 2, 10, 8));
		passwordForm.setBackground(PaletaCores.SUPERFICIE);
		passwordForm.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(18, 22, 18, 22)));
		passwordForm.add(rotulo("Senha actual"));
		passwordForm.add(currentPasswordField);
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
		JLabel notice = new JLabel("Os dados da conta sao carregados da base de dados.");
		notice.setForeground(PaletaCores.SECUNDARIA);
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

	/** Valida a senha actual e guarda a nova senha na base de dados. */
	private void alterarSenha() {
		char[] senhaActual = currentPasswordField.getPassword();
		char[] novaSenha = passwordField.getPassword();
		char[] confirmacao = confirmPasswordField.getPassword();
		try {
			if (senhaActual.length == 0) {
				JOptionPane.showMessageDialog(this, "Informe a senha actual.",
						"Senha em falta", JOptionPane.WARNING_MESSAGE);
				return;
			}
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
			if (!autenticacaoDAO.alterarSenha(usuarioId, new String(senhaActual), new String(novaSenha))) {
				JOptionPane.showMessageDialog(this, "A senha actual esta incorrecta ou a conta foi alterada.",
						"Senha nao alterada", JOptionPane.WARNING_MESSAGE);
				return;
			}
			currentPasswordField.setText("");
			passwordField.setText("");
			confirmPasswordField.setText("");
			JOptionPane.showMessageDialog(this, "Senha alterada com sucesso.");
		} catch (RuntimeException e) {
			Throwable causa = e.getCause() == null ? e : e.getCause();
			String mensagem = causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
			JOptionPane.showMessageDialog(this, "Nao foi possivel alterar a senha: " + mensagem,
					"Erro", JOptionPane.ERROR_MESSAGE);
		} finally {
			java.util.Arrays.fill(senhaActual, '\0');
			java.util.Arrays.fill(novaSenha, '\0');
			java.util.Arrays.fill(confirmacao, '\0');
		}
	}

}
