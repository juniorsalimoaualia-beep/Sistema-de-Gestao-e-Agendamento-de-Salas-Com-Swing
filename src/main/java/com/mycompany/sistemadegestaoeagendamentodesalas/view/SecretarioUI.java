package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import java.time.LocalTime;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public class SecretarioUI extends PerfilDashboardUI {
	private final SalaController salaController = new SalaController();
	private final ReservaController reservaController = new ReservaController();
	private final HorarioController horarioController = new HorarioController();
	private final CursoController cursoController = new CursoController();
	private final DisciplinaController disciplinaController = new DisciplinaController();
	private final DepartamentoController departamentoController = new DepartamentoController();
	private final Departamento departamento;

	/** Prepara o painel do secretario e carrega os dados do seu departamento. */
	public SecretarioUI(String email, int utilizadorId, Runnable voltarMenu) {
		super("SECRETARIO", email, utilizadorId, voltarMenu);
		departamento = utilizadorId <= 0 ? null
				: departamentoController.buscarPorEmailSecretario(email);
		adicionarSalas();
		adicionarReservas();
		adicionarHorarios();
		finalizarDashboard("SECRETARIO", departamento == null
				? "Sem departamento associado. Solicite ao administrador a atribuicao do departamento."
				: null);
	}

	/** Apresenta as salas pertencentes ao departamento do secretario. */
	private void adicionarSalas() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Nome", "Estado"});
		JTable table = new JTable(model);
		JPanel panel = criarPainelTabela("Salas do departamento", table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> {
			model.setRowCount(0);
			if (departamento != null) {
				for (Sala sala : salaController.listar()) {
					if (sala.getDepartamento() != null
							&& sala.getDepartamento().getId() == departamento.getId()) {
						model.addRow(new Object[]{sala.getId(), sala.getNome(), sala.getEstado()});
					}
				}
			}
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarSala());
		panel.putClientProperty("reload", reload);
		adicionarPainel("Salas", panel);
	}

	/** Apresenta e permite gerir reservas das salas do departamento associado. */
	private void adicionarReservas() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Sala", "Docente", "Disciplina",
				"Data", "Inicio", "Fim", "Tipo", "Estado"});
		JTable table = new JTable(model);
		aplicarCoresEstadoReserva(table, 8);
		JPanel panel = criarPainelTabela("Reservas", table);
		JButton atualizar = new JButton("Atualizar");
		JButton confirmar = new JButton("Confirmar");
		JButton recusar = new JButton("Recusar");
		JButton cancelar = new JButton("Cancelar");
		panel.add(criarAcoes(atualizar, confirmar, recusar, cancelar), java.awt.BorderLayout.SOUTH);
		Runnable reload = () -> {
			model.setRowCount(0);
			if (departamento == null) return;
			for (Reserva reserva : reservaController.listar()) {
				if (!pertenceAoDepartamento(reserva.getSalaId())) {
					continue;
				}
				model.addRow(new Object[]{reserva.getId(),
						reserva.getSalaId() == null ? "-" : reserva.getSalaId().getNome(),
						reserva.getDocenteId() == null ? "-" : reserva.getDocenteId().getNomeCompleto(),
						reserva.getDisciplina() == null ? "-" : reserva.getDisciplina().getNome(),
						reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(),
						reserva.getTipoReserva(), reserva.getEstadoReserva()});
			}
		};
		atualizar.addActionListener(event -> reload.run());
		confirmar.addActionListener(event -> atualizarEstadoReserva(table, reload, "confirmar"));
		recusar.addActionListener(event -> atualizarEstadoReserva(table, reload, "recusar"));
		cancelar.addActionListener(event -> atualizarEstadoReserva(table, reload, "cancelar"));
		panel.putClientProperty("reload", reload);
		adicionarPainel("Reservas", panel);
	}

	/** Apresenta horarios relacionados com o departamento do secretario. */
	private void adicionarHorarios() {
		DefaultTableModel model = modeloTabela(
				new String[]{"ID", "Dia", "Inicio", "Fim", "Curso", "Disciplina", "Sala"});
		JTable table = new JTable(model);
		JPanel panel = criarPainelTabela("Horarios do departamento", table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> {
			model.setRowCount(0);
			if (departamento != null) {
				for (Horario horario : horarioController.listar()) {
					if (pertenceAoDepartamento(horario.getCurso())) {
						model.addRow(new Object[]{horario.getId(), horario.getDiaSemana(),
								horario.getHoraInicio(), horario.getHoraFim(), horario.getNomeCurso(),
								horario.getDisciplina() == null ? "-" : horario.getDisciplina().getNome(),
								horario.getSala() == null ? "-" : horario.getSala().getNome()});
					}
				}
			}
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarHorario());
		panel.putClientProperty("reload", reload);
		adicionarPainel("Horarios", panel);
	}

	/** Regista um horario dentro do departamento do secretario. */
	private void criarHorario() {
		if (departamento == null) {
			mostrarErro("Nao e possivel criar horarios sem um departamento associado.");
			return;
		}
		java.util.List<Curso> cursosDepartamento = new java.util.ArrayList<>();
		for (Curso item : cursoController.listar()) {
			if (pertenceAoDepartamento(item)) cursosDepartamento.add(item);
		}
		JComboBox<Opcao<Curso>> curso = comboOpcoes(cursosDepartamento, Curso::getNome);
		if (curso.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Nao existem cursos associados ao seu departamento.",
					"Sem cursos", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<Opcao<Disciplina>> disciplina = new JComboBox<>();
		atualizarDisciplinasDoCurso(disciplina, valorSelecionado(curso));
		curso.addActionListener(event ->
				atualizarDisciplinasDoCurso(disciplina, valorSelecionado(curso)));
		java.util.List<Sala> salasDepartamento = new java.util.ArrayList<>();
		for (Sala sala : salaController.listar()) {
			if (sala.getDepartamento() != null
					&& sala.getDepartamento().getId() == departamento.getId()) {
				salasDepartamento.add(sala);
			}
		}
		JComboBox<Opcao<Sala>> sala = comboOpcoes(salasDepartamento, Sala::getNome);
		if (sala.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Registe uma sala no seu departamento antes do horario.",
					"Sem salas", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<DiaSemana> dia = new JComboBox<>(DiaSemana.values());
		JTextField inicio = new JTextField("08:00", 16);
		JTextField fim = new JTextField("09:00", 16);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		Object[] campos = {"Curso", curso, "Disciplina", disciplina, "Sala", sala, "Dia", dia,
				"Hora de inicio (HH:MM)", inicio, "Hora de fim (HH:MM)", fim};
		if (JOptionPane.showConfirmDialog(this, campos, "Novo horario",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
			return;
		}
		try {
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			executarOperacao("A guardar horario...", () -> horarioController.salvar(new Horario(
					horarioController.gerarProximoId(), valorSelecionado(curso),
					valorSelecionado(disciplina), valorSelecionado(sala), (DiaSemana) dia.getSelectedItem(),
					horaInicio, horaFim)), "Horario registado.");
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
	}

	/** Regista uma sala vinculada ao departamento do secretario. */
	private void criarSala() {
		if (departamento == null) {
			mostrarErro("Nao e possivel criar salas sem um departamento associado.");
			return;
		}
		JTextField nome = new JTextField();
		Validacao.filtrarTexto(nome, 100);
		if (JOptionPane.showConfirmDialog(this, new Object[]{"Nome da sala", nome}, "Nova sala",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}
		try {
			Validacao.validarTextoObrigatorio(nome.getText(), "nome da sala", 100);
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
			return;
		}
		executarOperacao("A guardar sala...", () -> {
			Sala sala = new Sala(salaController.gerarProximoId(), nome.getText().trim());
			sala.setDepartamento(departamento);
			salaController.salvar(sala);
		}, "Sala registada.");
	}

	/** Verifica se a sala pertence ao departamento do secretario atual. */
	private boolean pertenceAoDepartamento(Sala sala) {
		if (departamento == null || sala == null || sala.getDepartamento() == null) return false;
		return sala.getDepartamento().getId() == departamento.getId();
	}

	/** Verifica se o curso pertence ao departamento do secretario atual. */
	private boolean pertenceAoDepartamento(Curso curso) {
		return departamento != null && curso != null && curso.getDepartamento() != null
				&& curso.getDepartamento().getId() == departamento.getId();
	}

	/** Atualiza as disciplinas disponiveis conforme o curso escolhido. */
	private void atualizarDisciplinasDoCurso(JComboBox<Opcao<Disciplina>> combo, Curso curso) {
		combo.removeAllItems();
		combo.addItem(new Opcao<>(null, "Sem disciplina"));
		for (Disciplina disciplina : disciplinaController.listar()) {
			Curso cursoDisciplina = disciplina.getCurso();
			if (cursoDisciplina == null || cursoDisciplina.getId() <= 0
					|| curso != null && cursoDisciplina.getId() == curso.getId()) {
				combo.addItem(new Opcao<>(disciplina, disciplina.getNome()));
			}
		}
	}

	/** Confirma ou recusa a reserva selecionada e atualiza a tabela. */
	private void atualizarEstadoReserva(JTable table, Runnable reload, String operacao) {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Selecione uma reserva primeiro.",
					"Reserva nao selecionada", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int id = Integer.parseInt(table.getValueAt(row, 0).toString());
		boolean atualizado;
		switch (operacao) {
			case "confirmar":
				atualizado = reservaController.confirmar(id);
				break;
			case "recusar":
				atualizado = reservaController.recusar(id);
				break;
			default:
				atualizado = reservaController.cancelar(id);
				break;
		}
		if (atualizado) {
			JOptionPane.showMessageDialog(this, "Reserva " + operacao + " com sucesso.");
			reload.run();
		} else {
			JOptionPane.showMessageDialog(this, "Nao foi possivel " + operacao
					+ " a reserva. Verifique o estado atual e a disponibilidade da sala.",
					"Operacao nao realizada", JOptionPane.WARNING_MESSAGE);
		}
	}
}
