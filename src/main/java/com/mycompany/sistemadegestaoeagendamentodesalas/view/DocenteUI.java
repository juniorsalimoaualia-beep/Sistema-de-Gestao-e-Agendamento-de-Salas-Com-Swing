package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.InscricaoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoSala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.TipoReserva;

public class DocenteUI extends PerfilDashboardUI {
	private final DefaultTableModel reservasModel = modeloTabela(new String[]{"ID", "Sala", "Docente",
			"Disciplina", "Data", "Inicio", "Fim", "Tipo", "Estado"});
	private final DocenteController docenteController = new DocenteController();
	private final DisciplinaController disciplinaController = new DisciplinaController();
	private final DepartamentoController departamentoController = new DepartamentoController();
	private final SalaController salaController = new SalaController();
	private final ReservaController reservaController = new ReservaController();
	private final HorarioController horarioController = new HorarioController();
	private final InscricaoController inscricaoController = new InscricaoController();
	private final int docenteId;

	/** Prepara o painel do docente e carrega os dados guardados na base de dados. */
	public DocenteUI(String email, int utilizadorId, Runnable voltarMenu) {
		super("DOCENTE", email, utilizadorId, voltarMenu);
		docenteId = utilizadorId;
		if (utilizadorId <= 0 || docenteController.buscarPorId(utilizadorId) == null) {
			throw new IllegalStateException("A conta autenticada nao corresponde a um docente.");
		}
		adicionarDisciplinas();
		adicionarHorarios();
		adicionarReservas();
		adicionarEstudantesInscritos();
		finalizarDashboard("DOCENTE");
	}

	/** Apresenta as disciplinas associadas ao docente. */
	private void adicionarDisciplinas() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Disciplina", "Docente", "Curso"});
		for (Disciplina disciplina : disciplinaController.listarPorDocente(docenteId)) {
			model.addRow(new Object[]{disciplina.getId(), disciplina.getNome(),
					disciplina.getDocente() == null ? "-" : disciplina.getDocente().getNomeCompleto(),
					disciplina.getCurso() == null ? "Geral" : disciplina.getCurso().getNome()});
		}
		adicionarPainel("Disciplinas", criarPainelTabela("Disciplinas", new JTable(model)));
	}

	/** Apresenta os horarios associados as disciplinas do docente. */
	private void adicionarHorarios() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Dia", "Inicio", "Fim", "Curso", "Disciplina"});
		for (Disciplina disciplina : disciplinaController.listarPorDocente(docenteId)) {
			for (Horario horario : horarioController.listarPorDisciplina(disciplina.getId())) {
				model.addRow(new Object[]{horario.getId(), horario.getDiaSemana(), horario.getHoraInicio(),
						horario.getHoraFim(), horario.getCurso() == null ? "-" : horario.getCurso().getNome(),
						horario.getDisciplina() == null ? disciplina.getNome() : horario.getDisciplina().getNome()});
			}
		}
		adicionarPainel("Horarios", criarPainelTabela("Horarios", new JTable(model)));
	}

	/** Cria a tabela de reservas e as acoes disponiveis para o docente. */
	private void adicionarReservas() {
		JTable tabela = new JTable(reservasModel);
		aplicarCoresEstadoReserva(tabela, 8);
		JPanel panel = criarPainelTabela("Reservas", tabela);
		JButton atualizar = new JButton("Atualizar");
		JButton solicitar = new JButton("Solicitar reserva");
		JButton cancelar = new JButton("Cancelar reserva selecionada");
		cancelar.setEnabled(false);
		panel.add(criarAcoes(atualizar, solicitar, cancelar), BorderLayout.SOUTH);
		tabela.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				cancelar.setEnabled(reservaPodeSerCancelada(tabela));
			}
		});
		atualizar.addActionListener(event -> carregarReservas());
		solicitar.addActionListener(event -> solicitarReserva());
		cancelar.addActionListener(event -> cancelarReservaSelecionada(tabela, cancelar));
		carregarReservas();
		adicionarPainel("Reservas", panel);
	}

	/** Carrega da base de dados as reservas pertencentes ao docente. */
	private void carregarReservas() {
		reservasModel.setRowCount(0);
		for (Reserva reserva : reservaController.listar()) {
			if (reserva.getDocenteId() == null || reserva.getDocenteId().getId() != docenteId) continue;
			reservasModel.addRow(new Object[]{reserva.getId(),
					reserva.getSalaId() == null ? "-" : reserva.getSalaId().getNome(),
					reserva.getDocenteId().getNomeCompleto(),
					reserva.getDisciplina() == null ? "-" : reserva.getDisciplina().getNome(),
					reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(),
					reserva.getTipoReserva(), reserva.getEstadoReserva()});
		}
	}

	/** Indica se a reserva selecionada pode ser cancelada. */
	private boolean reservaPodeSerCancelada(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) return false;
		Object estado = tabela.getValueAt(linha, 8);
		return "PENDENTE".equals(estado) || "CONFIRMADA".equals(estado);
	}

	/** Confirma e executa o cancelamento da reserva selecionada. */
	private void cancelarReservaSelecionada(JTable tabela, JButton cancelar) {
		int linha = tabela.getSelectedRow();
		if (linha < 0 || !reservaPodeSerCancelada(tabela)) {
			cancelar.setEnabled(false);
			JOptionPane.showMessageDialog(this, "Selecione uma reserva pendente ou confirmada.",
					"Reserva indisponivel", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int resposta = JOptionPane.showConfirmDialog(this,
				"Tem a certeza de que deseja cancelar esta reserva?", "Confirmar cancelamento",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (resposta != JOptionPane.YES_OPTION) return;
		int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
		boolean cancelada;
		cancelada = reservaController.cancelarDoDocente(id, docenteId);
		if (cancelada) {
			JOptionPane.showMessageDialog(this, "Reserva cancelada.");
			carregarReservas();
		} else {
			JOptionPane.showMessageDialog(this, "Nao foi possivel cancelar a reserva.",
					"Cancelamento nao realizado", JOptionPane.WARNING_MESSAGE);
		}
		cancelar.setEnabled(false);
	}

	/** Recolhe dados e envia ou demonstra um pedido de reserva. */
	private void solicitarReserva() {
		List<Departamento> departamentos = departamentoController.listar();
		if (departamentos.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Nao existem departamentos registados.",
					"Sem departamentos", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<Opcao<Departamento>> departamento = comboOpcoes(departamentos, Departamento::getNome);
		JComboBox<Opcao<Sala>> sala = new JComboBox<>();
		List<Sala> salas = salaController.listar();
		JTextField data = new JTextField(LocalDate.now().plusDays(1).toString(), 16);
		JTextField inicio = new JTextField("08:00", 16);
		JTextField fim = new JTextField("09:00", 16);
		Validacao.filtrarDataHora(data, true);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		JLabel estadoSalas = new JLabel("Selecione departamento, data e horario.");
		atualizarSalasDisponiveis(departamento, sala, salas, data, inicio, fim, estadoSalas);
		departamento.addActionListener(event ->
				atualizarSalasDisponiveis(departamento, sala, salas, data, inicio, fim, estadoSalas));
		DocumentListener atualizarAoEditarHorario = new DocumentListener() {
			/** Atualiza a lista de salas apos texto ser inserido. */
			@Override
			public void insertUpdate(DocumentEvent event) { atualizar(); }
			/** Atualiza a lista de salas apos texto ser removido. */
			@Override
			public void removeUpdate(DocumentEvent event) { atualizar(); }
			/** Atualiza a lista de salas apos alteracoes no documento. */
			@Override
			public void changedUpdate(DocumentEvent event) { atualizar(); }
			/** Recalcula as salas disponiveis com os campos atuais. */
			private void atualizar() {
				atualizarSalasDisponiveis(departamento, sala, salas, data, inicio, fim, estadoSalas);
			}
		};
		data.getDocument().addDocumentListener(atualizarAoEditarHorario);
		inicio.getDocument().addDocumentListener(atualizarAoEditarHorario);
		fim.getDocument().addDocumentListener(atualizarAoEditarHorario);
		JComboBox<Opcao<Disciplina>> disciplina = new JComboBox<>();
		atualizarDisciplinasDocente(disciplina);
		JComboBox<TipoReserva> tipo = new JComboBox<>(TipoReserva.values());
		JLabel regraDias = new JLabel();
		Runnable atualizarRegraDias = () -> regraDias.setText(tipo.getSelectedItem() == TipoReserva.AULA
				? "Aula: segunda a sexta; sabado e permitido como excecao."
				: "Teste, exame e evento: de segunda a sabado.");
		tipo.addActionListener(event -> atualizarRegraDias.run());
		atualizarRegraDias.run();
		JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
		formulario.add(new JLabel("Departamento"));
		formulario.add(departamento);
		formulario.add(new JLabel("Sala disponivel"));
		formulario.add(sala);
		formulario.add(new JLabel(""));
		formulario.add(estadoSalas);
		formulario.add(new JLabel("Tipo"));
		formulario.add(tipo);
		formulario.add(new JLabel("Dias permitidos"));
		formulario.add(regraDias);
		formulario.add(new JLabel("Disciplina"));
		formulario.add(disciplina);
		formulario.add(new JLabel("Data (AAAA-MM-DD)"));
		formulario.add(data);
		formulario.add(new JLabel("Hora de inicio (HH:MM)"));
		formulario.add(inicio);
		formulario.add(new JLabel("Hora de fim (HH:MM)"));
		formulario.add(fim);
		mostrarFormulario("Solicitar reserva", formulario, () -> {
			Sala salaSelecionada = valorSelecionado(sala);
			if (salaSelecionada == null) {
				throw new IllegalArgumentException("Selecione uma sala disponivel no departamento escolhido.");
			}
			LocalDate dia = Validacao.validarDataIso(data.getText());
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			Departamento departamentoSelecionado = valorSelecionado(departamento);
			if (departamentoSelecionado == null || salaSelecionada.getDepartamento() == null
					|| salaSelecionada.getDepartamento().getId() != departamentoSelecionado.getId()) {
				throw new IllegalArgumentException("A sala nao pertence ao departamento selecionado.");
			}
			if (dia.isBefore(LocalDate.now())) {
				throw new IllegalArgumentException("A data da reserva nao pode estar no passado.");
			}
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			Disciplina disciplinaEscolhida = valorSelecionado(disciplina);
			if (tipo.getSelectedItem() == TipoReserva.AULA
					&& disciplinaEscolhida == null) {
				throw new IllegalArgumentException("Selecione a disciplina para uma reserva do tipo AULA.");
			}
			DiaSemana diaSemana = converterDia(dia.getDayOfWeek());
			TipoReserva tipoSelecionado = (TipoReserva) tipo.getSelectedItem();
			if (diaSemana == null || tipoSelecionado == null
					|| !tipoSelecionado.permiteDia(diaSemana)) {
				throw new IllegalArgumentException("As reservas nao podem ser marcadas ao domingo.");
			}
			Docente docente = docenteController.buscarPorId(docenteId);
			if (docente == null) {
				throw new IllegalStateException("Nao foi possivel localizar a conta do docente.");
			}
			Reserva reserva = new Reserva(reservaController.gerarProximoId(), salaSelecionada,
					docente, disciplinaEscolhida, null, dia, horaInicio, horaFim,
					tipoSelecionado, diaSemana);
			if (!reservaController.salvar(reserva)) {
				throw new IllegalArgumentException("A sala deixou de estar disponivel ou existe conflito.");
			}
			carregarReservas();
			mostrarInformacao("Pedido de reserva enviado ao secretario do departamento.");
			return true;
		});
	}

	/** Filtra salas do departamento que estao livres no periodo informado. */
	private void atualizarSalasDisponiveis(JComboBox<Opcao<Departamento>> departamento,
			JComboBox<Opcao<Sala>> comboSalas, List<Sala> salas, JTextField data,
			JTextField inicio, JTextField fim, JLabel estadoSalas) {
		Departamento selecionado = valorSelecionado(departamento);
		comboSalas.removeAllItems();
		if (selecionado == null) {
			estadoSalas.setText("Selecione um departamento.");
			return;
		}
		LocalDate dia;
		LocalTime horaInicio;
		LocalTime horaFim;
		try {
			dia = Validacao.validarDataIso(data.getText());
			horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				estadoSalas.setText("A hora final deve ser posterior a hora inicial.");
				return;
			}
		} catch (IllegalArgumentException e) {
			estadoSalas.setText("Informe data e horas validas para listar salas.");
			return;
		}
		int quantidade = 0;
		for (Sala item : salas) {
			if (item.getDepartamento() == null
					|| item.getDepartamento().getId() != selecionado.getId()
					|| item.getEstado() == EstadoSala.MANUTENCAO) {
				continue;
			}
			if (!reservaController.verificarDisponibilidade(
					item.getNome(), dia, horaInicio, horaFim)) {
				continue;
			}
			comboSalas.addItem(new Opcao<>(item, item.getNome()));
			quantidade++;
		}
		if (quantidade == 0) {
			estadoSalas.setText("Nao ha salas disponiveis nesse departamento e horario.");
		} else {
			estadoSalas.setText(quantidade + " sala(s) disponivel(is) no departamento.");
			comboSalas.setSelectedIndex(0);
		}
	}

	/** Carrega as disciplinas atribuidas ao docente para o formulario de reserva. */
	private void atualizarDisciplinasDocente(JComboBox<Opcao<Disciplina>> combo) {
		combo.removeAllItems();
		combo.addItem(new Opcao<>(null, "Sem disciplina"));
		for (Disciplina disciplina : disciplinaController.listarPorDocente(docenteId)) {
			combo.addItem(new Opcao<>(disciplina, disciplina.getNome()));
		}
	}

	/** Converte o dia da semana da data para o enum usado pelo sistema. */
	private DiaSemana converterDia(DayOfWeek dia) {
		switch (dia) {
			case MONDAY: return DiaSemana.SEGUNDA;
			case TUESDAY: return DiaSemana.TERCA;
			case WEDNESDAY: return DiaSemana.QUARTA;
			case THURSDAY: return DiaSemana.QUINTA;
			case FRIDAY: return DiaSemana.SEXTA;
			case SATURDAY: return DiaSemana.SABADO;
			default: return null;
		}
	}

	/** Apresenta os estudantes inscritos nas disciplinas do docente. */
	private void adicionarEstudantesInscritos() {
		DefaultTableModel model = modeloTabela(new String[]{
				"Disciplina", "ID estudante", "Estudante", "E-mail"});
		for (Disciplina disciplina : disciplinaController.listarPorDocente(docenteId)) {
			for (Inscricao inscricao : inscricaoController.listarPorDisciplina(disciplina.getId())) {
				if (inscricao.getEstudante() == null) continue;
				model.addRow(new Object[]{disciplina.getNome(), inscricao.getEstudante().getId(),
						inscricao.getEstudante().getNomeCompleto(), inscricao.getEstudante().getEmail()});
			}
		}
		adicionarPainel("Estudantes inscritos",
				criarPainelTabela("Estudantes inscritos", new JTable(model)));
	}
}
