package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class HorarioGestaoUI {
	private HorarioGestaoUI() {
	}

	static JButton[] criarBotoes(PerfilDashboardUI painel, HorarioController controller,
			Supplier<List<Horario>> carregarHorarios, Supplier<List<Sala>> carregarSalas) {
		JButton alterar = new JButton("Alterar horario");
		JButton trocar = new JButton("Trocar horarios entre docentes");
		alterar.addActionListener(event -> alterar(painel, controller, carregarHorarios, carregarSalas));
		trocar.addActionListener(event -> trocar(painel, controller, carregarHorarios));
		return new JButton[]{alterar, trocar};
	}

	static void ajustarLarguras(JButton... botoes) {
		int largura = 0;
		for (JButton botao : botoes) {
			largura = Math.max(largura, botao.getPreferredSize().width);
		}
		for (JButton botao : botoes) {
			Dimension preferido = botao.getPreferredSize();
			botao.setPreferredSize(new Dimension(largura, preferido.height));
		}
	}

	static SeletorDia criarSeletorDia(DiaSemana inicial) {
		return new SeletorDia(inicial);
	}

	private static void alterar(PerfilDashboardUI painel, HorarioController controller,
			Supplier<List<Horario>> carregarHorarios, Supplier<List<Sala>> carregarSalas) {
		List<Horario> horarios = carregarHorarios.get();
		if (horarios.isEmpty()) {
			painel.mostrarInformacao("Nao existem horarios para alterar.");
			return;
		}
		JComboBox<PerfilDashboardUI.Opcao<Horario>> horario =
				painel.comboOpcoes(horarios, HorarioGestaoUI::descricao);
		SeletorDia dia = criarSeletorDia(DiaSemana.SEGUNDA);
		JTextField inicio = new JTextField(5);
		JTextField fim = new JTextField(5);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		JComboBox<PerfilDashboardUI.Opcao<Sala>> sala = new JComboBox<>();
		Runnable preencher = () -> {
			Horario selecionado = painel.valorSelecionado(horario);
			if (selecionado == null) return;
			dia.definir(selecionado.getDiaSemana());
			inicio.setText(selecionado.getHoraInicio().toString());
			fim.setText(selecionado.getHoraFim().toString());
			sala.removeAllItems();
			List<Sala> salas = new ArrayList<>(carregarSalas.get());
			if (selecionado.getSala() != null
					&& salas.stream().noneMatch(item -> item.getId() == selecionado.getSala().getId())) {
				salas.add(selecionado.getSala());
			}
			for (Sala item : salas) {
				sala.addItem(new PerfilDashboardUI.Opcao<>(item, item.getNome()));
				if (selecionado.getSala() != null
						&& item.getId() == selecionado.getSala().getId()) {
					sala.setSelectedIndex(sala.getItemCount() - 1);
				}
			}
		};
		horario.addActionListener(event -> preencher.run());
		preencher.run();

		JPanel formulario = formulario(
				"Aula", horario,
				"Dia", dia.componente(),
				"Inicio (HH:MM)", inicio,
				"Fim (HH:MM)", fim,
				"Sala", sala);
		painel.mostrarFormulario("Alterar horario", formulario, () -> {
			Horario selecionado = painel.valorSelecionado(horario);
			Sala salaSelecionada = painel.valorSelecionado(sala);
			if (selecionado == null || salaSelecionada == null) {
				throw new IllegalArgumentException("Selecione uma aula e uma sala.");
			}
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			Horario atualizado = new Horario(selecionado.getId(), selecionado.getCurso(),
					selecionado.getDisciplina(), salaSelecionada, dia.diaSelecionado(),
					horaInicio, horaFim);
			return painel.executarOperacao("A alterar horario...",
					() -> {
						if (!controller.atualizar(atualizado)) {
							throw new IllegalStateException("O horario selecionado ja nao existe.");
						}
					}, "Horario alterado.");
		}, 620);
	}

	private static void trocar(PerfilDashboardUI painel, HorarioController controller,
			Supplier<List<Horario>> carregarHorarios) {
		List<Horario> horarios = carregarHorarios.get();
		if (horarios.size() < 2) {
			painel.mostrarInformacao("Sao necessarios dois horarios para efetuar a troca.");
			return;
		}
		JComboBox<PerfilDashboardUI.Opcao<Horario>> primeiro =
				painel.comboOpcoes(horarios, HorarioGestaoUI::descricao);
		JComboBox<PerfilDashboardUI.Opcao<Horario>> segundo =
				painel.comboOpcoes(horarios, HorarioGestaoUI::descricao);
		segundo.setSelectedIndex(1);
		JPanel formulario = formulario("Primeira aula", primeiro, "Segunda aula", segundo);
		painel.mostrarFormulario("Trocar horarios entre docentes", formulario, () -> {
			Horario aula1 = painel.valorSelecionado(primeiro);
			Horario aula2 = painel.valorSelecionado(segundo);
			if (aula1 == null || aula2 == null || aula1.getId() == aula2.getId()) {
				throw new IllegalArgumentException("Selecione duas aulas diferentes para trocar.");
			}
			if (aula1.getDisciplina() == null || aula2.getDisciplina() == null
					|| aula1.getDisciplina().getDocente() == null
					|| aula2.getDisciplina().getDocente() == null
					|| aula1.getDisciplina().getDocente().getId()
							== aula2.getDisciplina().getDocente().getId()) {
				throw new IllegalArgumentException("Selecione aulas atribuidas a dois docentes diferentes.");
			}
			return painel.executarOperacao("A trocar horarios...",
					() -> {
						if (!controller.trocarHorarios(aula1.getId(), aula2.getId())) {
							throw new IllegalStateException("Um dos horarios selecionados ja nao existe.");
						}
					}, "Horarios trocados.");
		});
	}

	private static String descricao(Horario horario) {
		String disciplina = horario.getDisciplina() == null
				? "Sem disciplina" : horario.getDisciplina().getNome();
		String docente = horario.getDisciplina() == null
				|| horario.getDisciplina().getDocente() == null
						? "Docente por definir" : horario.getDisciplina().getDocente().getNomeCompleto();
		String curso = horario.getCurso() == null ? "Curso por definir" : horario.getCurso().getNome();
		String sala = horario.getSala() == null ? "Sala por definir" : horario.getSala().getNome();
		return "#" + horario.getId() + " | " + curso + " | " + disciplina + " | " + docente + " | "
				+ horario.getDiaSemana() + " " + horario.getHoraInicio() + "-"
				+ horario.getHoraFim() + " | " + sala;
	}

	private static JPanel formulario(Object... campos) {
		JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
		for (int indice = 0; indice < campos.length; indice += 2) {
			formulario.add(new JLabel((String) campos[indice]));
			formulario.add((java.awt.Component) campos[indice + 1]);
		}
		return formulario;
	}

	static final class SeletorDia {
		private final JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		private final JCheckBox sabado = new JCheckBox("Aula excecional ao sabado");
		private final JComboBox<DiaSemana> dia = new JComboBox<>();
		private boolean atualizando;

		private SeletorDia(DiaSemana inicial) {
			painel.setOpaque(false);
			sabado.setOpaque(false);
			painel.add(dia);
			painel.add(sabado);
			sabado.addActionListener(event -> {
				if (atualizando) return;
				DiaSemana selecionado = (DiaSemana) dia.getSelectedItem();
				definir(sabado.isSelected() ? DiaSemana.SABADO
						: selecionado == DiaSemana.SABADO ? DiaSemana.SEGUNDA : selecionado);
			});
			definir(inicial == null ? DiaSemana.SEGUNDA : inicial);
		}

		private void definir(DiaSemana valor) {
			atualizando = true;
			sabado.setSelected(valor == DiaSemana.SABADO);
			dia.removeAllItems();
			for (DiaSemana opcao : DiaSemana.values()) {
				if (opcao != DiaSemana.SABADO || sabado.isSelected()) dia.addItem(opcao);
			}
			dia.setSelectedItem(valor == DiaSemana.SABADO ? DiaSemana.SABADO
					: valor == null ? DiaSemana.SEGUNDA : valor);
			atualizando = false;
		}

		JPanel componente() {
			return painel;
		}

		DiaSemana diaSelecionado() {
			DiaSemana selecionado = (DiaSemana) dia.getSelectedItem();
			if (selecionado == null) {
				throw new IllegalArgumentException("Selecione um dia para a aula.");
			}
			return selecionado;
		}
	}
}
