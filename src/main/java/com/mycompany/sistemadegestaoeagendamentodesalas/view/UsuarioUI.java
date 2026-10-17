package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UsuarioUI extends JPanel {
	public UsuarioUI(String perfil, String email) {
		super(new BorderLayout(0, 12));
		setBackground(new Color(246, 247, 241));
		setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
		JLabel title = new JLabel("Sessao ativa");
		title.setFont(new Font("Segoe UI", Font.BOLD, 20));
		title.setForeground(new Color(31, 49, 45));
		JLabel details = new JLabel("Perfil: " + perfil + "     E-mail: " + email);
		details.setForeground(new Color(75, 91, 82));
		add(title, BorderLayout.NORTH);
		add(details, BorderLayout.CENTER);
	}
}