package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

final class HorariosPorAnoUI {
	private HorariosPorAnoUI() {
	}

	static JPanel criar(List<Horario> horarios, boolean agruparPorCurso) {
		JPanel painel = new JPanel();
		painel.setOpaque(false);
		painel.setLayout(new javax.swing.BoxLayout(painel, javax.swing.BoxLayout.Y_AXIS));
		Map<String, Map<Integer, List<Horario>>> cursos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Horario horario : horarios) {
			String curso = horario.getCurso() == null || horario.getCurso().getNome().isBlank()
					? "Curso nao definido" : horario.getCurso().getNome();
			int ano = horario.getDisciplina() == null ? 0 : horario.getDisciplina().getAno();
			cursos.computeIfAbsent(curso, chave -> new TreeMap<>())
					.computeIfAbsent(ano, chave -> new ArrayList<>()).add(horario);
		}

		for (Map.Entry<String, Map<Integer, List<Horario>>> curso : cursos.entrySet()) {
			if (agruparPorCurso) painel.add(titulo(curso.getKey()));
			for (Map.Entry<Integer, List<Horario>> ano : curso.getValue().entrySet()) {
				JPanel secaoAno = new JPanel(new BorderLayout(0, 8));
				secaoAno.setBackground(Color.WHITE);
				secaoAno.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(PaletaCores.BORDA),
						BorderFactory.createEmptyBorder(12, 14, 14, 14)));
				secaoAno.add(titulo(ano.getKey() == 0 ? "Ano nao definido"
						: ano.getKey() + "º ano"), BorderLayout.NORTH);

				List<Horario> diasUteis = new ArrayList<>();
				List<Horario> sabado = new ArrayList<>();
				for (Horario horario : ano.getValue()) {
					(horario.getDiaSemana().name().equals("SABADO") ? sabado : diasUteis).add(horario);
				}
				JPanel tabelas = new JPanel();
				tabelas.setOpaque(false);
				tabelas.setLayout(new javax.swing.BoxLayout(tabelas, javax.swing.BoxLayout.Y_AXIS));
				if (!diasUteis.isEmpty()) tabelas.add(criarTabela(diasUteis));
				if (!sabado.isEmpty()) {
					JLabel excecao = new JLabel("Excecao - sabado");
					excecao.setFont(new Font("Segoe UI", Font.BOLD, 14));
					excecao.setForeground(PaletaCores.SECUNDARIA);
					excecao.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
					tabelas.add(excecao);
					tabelas.add(criarTabela(sabado));
				}
				secaoAno.add(tabelas, BorderLayout.CENTER);
				painel.add(secaoAno);
				painel.add(javax.swing.Box.createVerticalStrut(12));
			}
		}
		if (horarios.isEmpty()) {
			JLabel vazio = new JLabel("Ainda nao existem horarios registados.");
			vazio.setForeground(PaletaCores.SECUNDARIA);
			painel.add(vazio);
		}
		return painel;
	}

	private static JLabel titulo(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(new Font("Segoe UI", Font.BOLD, 17));
		label.setForeground(PaletaCores.TEXTO);
		label.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
		return label;
	}

	private static JScrollPane criarTabela(List<Horario> horarios) {
		horarios.sort(Comparator.comparing(Horario::getDiaSemana)
				.thenComparing(Horario::getHoraInicio));
		DefaultTableModel modelo = new DefaultTableModel(
				new String[]{"Dia", "Inicio", "Fim", "Disciplina", "Docente", "Sala"}, 0) {
			@Override
			public boolean isCellEditable(int linha, int coluna) {
				return false;
			}
		};
		for (Horario horario : horarios) {
			Disciplina disciplina = horario.getDisciplina();
			modelo.addRow(new Object[]{horario.getDiaSemana(),
					horario.getHoraInicio(), horario.getHoraFim(),
					disciplina == null ? "-" : disciplina.getNome(),
					disciplina == null || disciplina.getDocente() == null
							? "Por definir" : disciplina.getDocente().getNomeCompleto(),
					horario.getSala() == null ? "Por definir" : horario.getSala().getNome()});
		}
		JTable tabela = new JTable(modelo);
		tabela.setRowHeight(28);
		tabela.setFillsViewportHeight(true);
		tabela.setPreferredScrollableViewportSize(new java.awt.Dimension(760,
				Math.max(58, Math.min(280, (horarios.size() + 1) * 28))));
		JScrollPane scroll = new JScrollPane(tabela);
		scroll.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		return scroll;
	}
}
