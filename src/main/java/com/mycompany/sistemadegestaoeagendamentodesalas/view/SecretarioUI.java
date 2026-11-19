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
		Runnable reload = () -> executarAtualizacao(atualizar, salaController::listar, salas -> {
			model.setRowCount(0);
			if (departamento != null) {
				for (Sala sala : salas) {
					if (sala.getDepartamento() != null
							&& sala.getDepartamento().getId() == departamento.getId()) {
						model.addRow(new Object[]{sala.getId(), sala.getNome(), sala.getEstado()});
					}
				}
			}
		});
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarSala());
		panel.putClientProperty("reload", reload);
		atualizarPeriodicamenteQuandoVisivel(panel, reload, 60_000);
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
		mostrarAcoesAoSelecionar(table, confirmar, recusar, cancelar);
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				int row = table.getSelectedRow();
				cancelar.setEnabled(row >= 0 && !"EXPIRADA".equals(table.getValueAt(row, 8)));
			}
		});
		Runnable reload = () -> executarAtualizacao(atualizar, reservaController::listar, reservas -> {
			model.setRowCount(0);
			if (departamento == null) return;
			for (Reserva reserva : reservas) {
				if (!pertenceAoDepartamento(reserva.getSalaId())) {
					continue;
				}
				model.addRow(new Object[]{reserva.getId(),
						reserva.getSalaId() == null ? "-" : reserva.getSalaId().getNome(),
						reserva.getDocenteId() == null ? "-" : reserva.getDocenteId().getNomeCompleto(),
						reserva.getDisciplina() == null ? "-" : reserva.getDisciplina().getNome(),
						reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(),
						reserva.getTipoReserva(), reserva.getEstadoExibicao()});
			}
		});
		atualizar.addActionListener(event -> reload.run());
		confirmar.addActionListener(event -> atualizarEstadoReserva(table, "confirmar"));
		recusar.addActionListener(event -> atualizarEstadoReserva(table, "recusar"));
		cancelar.addActionListener(event -> atualizarEstadoReserva(table, "cancelar"));
		panel.putClientProperty("reload", reload);
		atualizarPeriodicamenteQuandoVisivel(panel, reload, 60_000);
		adicionarPainel("Reservas", panel);
	}

	/** Apresenta horarios relacionados com o departamento do secretario. */
	private void adicionarHorarios() {
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 24, 24));
		javax.swing.JLabel titulo = new javax.swing.JLabel("Horarios por curso e ano");
		titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
		titulo.setForeground(PaletaCores.TEXTO);
		panel.add(titulo, BorderLayout.NORTH);
		JPanel grupos = new JPanel(new BorderLayout());
		grupos.setOpaque(false);
		panel.add(new JScrollPane(grupos), BorderLayout.CENTER);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		JButton[] acoesHorario = HorarioGestaoUI.criarBotoes(
				this, horarioController, this::horariosDoDepartamento, this::salasDoDepartamento);
		JPanel acoes = criarAcoes(atualizar, adicionar, acoesHorario[0], acoesHorario[1]);
		HorarioGestaoUI.ajustarLarguras(acoesHorario);
		panel.add(acoes, BorderLayout.SOUTH);
		Runnable reload = () -> executarAtualizacao(atualizar, this::horariosDoDepartamento, horarios -> {
			grupos.removeAll();
			grupos.add(HorariosPorAnoUI.criar(horarios, true), BorderLayout.NORTH);
			grupos.revalidate();
			grupos.repaint();
		});
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarHorario());
		panel.putClientProperty("reload", reload);
		adicionarPainel("Horarios", panel);
	}

	private java.util.List<Horario> horariosDoDepartamento() {
		java.util.List<Horario> horarios = new java.util.ArrayList<>();
		if (departamento != null) {
			for (Horario horario : horarioController.listar()) {
				if (pertenceAoDepartamento(horario.getCurso())) horarios.add(horario);
			}
		}
		return horarios;
	}

	private java.util.List<Sala> salasDoDepartamento() {
		java.util.List<Sala> salas = new java.util.ArrayList<>();
		if (departamento != null) {
			for (Sala sala : salaController.listar()) {
				if (pertenceAoDepartamento(sala)) salas.add(sala);
			}
		}
		return salas;
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
		HorarioGestaoUI.SeletorDia dia = HorarioGestaoUI.criarSeletorDia(DiaSemana.SEGUNDA);
		JTextField inicio = new JTextField("08:00", 16);
		JTextField fim = new JTextField("09:00", 16);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		JPanel formulario = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
		formulario.add(new javax.swing.JLabel("Curso"));
		formulario.add(curso);
		formulario.add(new javax.swing.JLabel("Disciplina"));
		formulario.add(disciplina);
		formulario.add(new javax.swing.JLabel("Sala"));
		formulario.add(sala);
		formulario.add(new javax.swing.JLabel("Dia"));
		formulario.add(dia.componente());
		formulario.add(new javax.swing.JLabel("Hora de inicio (HH:MM)"));
		formulario.add(inicio);
		formulario.add(new javax.swing.JLabel("Hora de fim (HH:MM)"));
		formulario.add(fim);
		mostrarFormulario("Novo horario", formulario, () -> {
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			Curso cursoSelecionado = valorSelecionado(curso);
			Disciplina disciplinaSelecionada = valorSelecionado(disciplina);
			Sala salaSelecionada = valorSelecionado(sala);
			DiaSemana diaSelecionado = dia.diaSelecionado();
			return executarOperacao("A guardar horario...", () -> horarioController.salvar(new Horario(
					horarioController.gerarProximoId(), cursoSelecionado,
					disciplinaSelecionada, salaSelecionada, diaSelecionado,
					horaInicio, horaFim)), "Horario registado.");
		});
	}

	/** Regista uma sala vinculada ao departamento do secretario. */
	private void criarSala() {
		if (departamento == null) {
			mostrarErro("Nao e possivel criar salas sem um departamento associado.");
			return;
		}
		JTextField nome = new JTextField();
		Validacao.filtrarTexto(nome, 100);
		JPanel formulario = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
		formulario.add(new javax.swing.JLabel("Nome da sala"));
		formulario.add(nome);
		mostrarFormulario("Nova sala", formulario, () -> {
			String nomeSala = Validacao.validarNomeSala(nome.getText());
			return executarOperacao("A guardar sala...", () -> {
				Sala sala = new Sala(salaController.gerarProximoId(), nomeSala);
				sala.setDepartamento(departamento);
				salaController.salvar(sala);
			}, "Sala registada.");
		});
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
	private void atualizarEstadoReserva(JTable table, String operacao) {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Selecione uma reserva primeiro.",
					"Reserva nao selecionada", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if ("cancelar".equals(operacao) && "EXPIRADA".equals(table.getValueAt(row, 8))) {
			JOptionPane.showMessageDialog(this, "Uma reserva expirada nao pode ser cancelada.",
					"Reserva expirada", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int id = Integer.parseInt(table.getValueAt(row, 0).toString());
		executarOperacao("A atualizar reserva...", () -> {
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
			if (!atualizado) {
				throw new IllegalStateException("Nao foi possivel " + operacao
						+ " a reserva. Verifique o estado atual e a disponibilidade da sala.");
			}
		}, "Reserva " + operacao + " com sucesso.");
	}
}
