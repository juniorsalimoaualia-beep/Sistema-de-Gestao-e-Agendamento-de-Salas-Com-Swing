package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.Controller;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.InscricaoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.TurmaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Secretario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Turma;
import com.mycompany.sistemadegestaoeagendamentodesalas.service.EmailGenarator;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.DefaultListModel;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.ListSelectionModel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AdminUI extends PerfilDashboardUI {
	private final SalaController salaController = new SalaController();
	private final CursoController cursoController = new CursoController();
	private final DepartamentoController departamentoController = new DepartamentoController();
	private final DisciplinaController disciplinaController = new DisciplinaController();
	private final DocenteController docenteController = new DocenteController();
	private final EstudanteController estudanteController = new EstudanteController();
	private final HorarioController horarioController = new HorarioController();
	private final InscricaoController inscricaoController = new InscricaoController();
	private final SecretarioController secretarioController = new SecretarioController();
	private final TurmaController turmaController = new TurmaController();
	private final EmailGenarator emGen= new EmailGenarator();

	/** Monta o painel administrativo e regista as suas seccoes de gestao. */
	public AdminUI(String perfil, String email, int utilizadorId, Runnable voltarMenu) {
		super(validarPerfil(perfil), email, utilizadorId, voltarMenu);
		adicionarSalas();
		adicionarCursos();
		adicionarDepartamentos();
		adicionarDisciplinas();
		adicionarDocentes();
		adicionarEstudantes();
		adicionarSecretarios();
		adicionarTurmas();
		adicionarHorarios();
		adicionarInscricoes();
		finalizarDashboard("ADMIN");
	}

	/** Verifica se o codigo corresponde ao perfil de administrador. */
	private static String validarPerfil(String perfil) {
		if (!"ADMIN".equalsIgnoreCase(perfil)) {
			throw new IllegalArgumentException("Perfil desconhecido para AdminUI: " + perfil);
		}
		return "ADMIN";
	}

	/** Monta a tabela de salas com filtro por departamento. */
	private void adicionarSalas() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Nome", "Estado", "Departamento"});
		JTable table = new JTable(model);
		JComboBox<Opcao<Departamento>> filtro = opcoes(departamentoController.listar(),
				Departamento::getNome, "Todos os departamentos");
		boolean[] atualizandoFiltro = {false};
		JPanel panel = painelComFiltro("Salas", "Departamento:", filtro, table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		JButton alterarDepartamento = new JButton("Alterar departamento");
		JButton eliminar = new JButton("Eliminar sala");
		panel.add(criarAcoes(atualizar, adicionar, alterarDepartamento, eliminar), BorderLayout.SOUTH);
		mostrarAcoesAoSelecionar(table, alterarDepartamento, eliminar);
		Runnable reload = () -> {
			executarAtualizacao(atualizar,
					() -> new java.util.AbstractMap.SimpleEntry<>(
							departamentoController.listar(), salaController.listar()),
					dados -> {
						atualizarOpcoes(filtro, dados.getKey(), Departamento::getNome,
								"Todos os departamentos", atualizandoFiltro);
						Departamento selecionado = valorSelecionado(filtro);
						model.setRowCount(0);
						for (Sala sala : dados.getValue()) {
							if (selecionado == null || sala.getDepartamento() != null
									&& sala.getDepartamento().getId() == selecionado.getId()) {
								model.addRow(new Object[]{sala.getId(), sala.getNome(), sala.getEstado(),
										sala.getDepartamento() == null ? "-" : sala.getDepartamento().getNome()});
							}
						}
					});
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarSala());
		alterarDepartamento.addActionListener(event -> alterarDepartamentoSala(table));
		eliminar.addActionListener(event -> eliminarSala(table));
		filtro.addActionListener(event -> {
			if (!atualizandoFiltro[0]) reload.run();
		});
		panel.putClientProperty("reload", reload);
		atualizarPeriodicamenteQuandoVisivel(panel, reload, 60_000);
		adicionarPainel("Salas", panel);
	}

	/** Adiciona a seccao de consulta e criacao de cursos. */
	private void adicionarCursos() {
		adicionarTabela("Cursos", new String[]{"ID", "Nome", "Departamento"}, cursoController,
				curso -> new Object[]{curso.getId(), curso.getNome(),
						curso.getDepartamento() == null ? "-" : curso.getDepartamento().getNome()},
				this::criarCurso);
	}

	/** Monta a tabela de departamentos e as acoes de atribuicao de secretario. */
	private void adicionarDepartamentos() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Nome", "Salas", "Secretario"});
		JTable table = new JTable(model);
		JPanel panel = criarPainelTabela("Departamentos e secretarios", table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar departamento");
		JButton atribuir = new JButton("Atribuir secretario");
		panel.add(criarAcoes(atualizar, adicionar, atribuir), BorderLayout.SOUTH);
		mostrarAcoesAoSelecionar(table, atribuir);
		Runnable reload = () -> executarAtualizacao(atualizar,
				() -> new java.util.AbstractMap.SimpleEntry<>(
						departamentoController.listar(), salaController.listar()),
				dados -> {
					model.setRowCount(0);
					for (Departamento departamento : dados.getKey()) {
						List<String> nomesSalas = new ArrayList<>();
						for (Sala sala : dados.getValue()) {
							if (sala.getDepartamento() != null
									&& sala.getDepartamento().getId() == departamento.getId()) {
								nomesSalas.add(sala.getNome());
							}
						}
						model.addRow(new Object[]{departamento.getId(), departamento.getNome(),
								nomesSalas.isEmpty() ? "Sem salas" : String.join(", ", nomesSalas),
								departamento.getSecretario() == null ? "Por atribuir"
										: departamento.getSecretario().getNomeCompleto()});
					}
				});
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarDepartamento());
		atribuir.addActionListener(event -> atribuirSecretario(table));
		panel.putClientProperty("reload", reload);
		adicionarPainel("Departamentos", panel);
	}

	/** Monta filtros para consultar disciplinas por docente e disciplina. */
	private void adicionarDisciplinas() {
		DefaultTableModel model = modeloTabela(
				new String[]{"ID", "Disciplina", "Docente", "Tipo / Curso", "Ano"});
		JTable table = new JTable(model);
		JComboBox<String> filtroDocente = new JComboBox<>();
		filtroDocente.addItem("Todos os docentes");
		filtroDocente.addItem("Sem docente");
		for (Docente docente : docenteController.listar()) {
			filtroDocente.addItem(docente.getNomeCompleto());
		}
		JComboBox<String> filtroDisciplina = new JComboBox<>();
		filtroDisciplina.addItem("Todas as disciplinas");
		for (Disciplina disciplina : disciplinaController.listar()) {
			filtroDisciplina.addItem(disciplina.getNome());
		}
		boolean[] atualizandoFiltros = {false};
		JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		filtros.setOpaque(false);
		filtros.add(new JLabel("Docente:"));
		filtros.add(filtroDocente);
		filtros.add(new JLabel("Disciplina:"));
		filtros.add(filtroDisciplina);
		JPanel panel = painelPersonalizado("Disciplinas e docentes", filtros, table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		JButton associarDocente = new JButton("Associar docente");
		JButton removerDocente = new JButton("Remover docente");
		JButton remover = new JButton("Remover");
		panel.add(criarAcoes(atualizar, adicionar, associarDocente, removerDocente, remover),
				BorderLayout.SOUTH);
		mostrarAcoesAoSelecionar(table, associarDocente, removerDocente, remover);
		Runnable reload = () -> {
			executarAtualizacao(atualizar,
					() -> new java.util.AbstractMap.SimpleEntry<>(
							docenteController.listar(), disciplinaController.listar()),
					dados -> {
						List<String> docentes = new ArrayList<>();
						for (Docente docente : dados.getKey()) docentes.add(docente.getNomeCompleto());
						List<String> disciplinas = new ArrayList<>();
						for (Disciplina disciplina : dados.getValue()) disciplinas.add(disciplina.getNome());
						atualizarOpcoesTexto(filtroDocente, docentes, "Todos os docentes",
								true, atualizandoFiltros);
						atualizarOpcoesTexto(filtroDisciplina, disciplinas, "Todas as disciplinas",
								false, atualizandoFiltros);
						String docenteSelecionado = (String) filtroDocente.getSelectedItem();
						String disciplinaSelecionada = (String) filtroDisciplina.getSelectedItem();
						model.setRowCount(0);
						for (Disciplina disciplina : dados.getValue()) {
							String docente = disciplina.getDocente() == null
									? "Sem docente" : disciplina.getDocente().getNomeCompleto();
							String nomeCurso = disciplina.getCurso() == null
									|| disciplina.getCurso().getId() <= 0
									? "Disciplina geral" : disciplina.getCurso().getNome();
							boolean coincideDocente = docenteSelecionado == null
									|| "Todos os docentes".equals(docenteSelecionado)
									|| docenteSelecionado.equals(docente);
							boolean coincideDisciplina = disciplinaSelecionada == null
									|| "Todas as disciplinas".equals(disciplinaSelecionada)
									|| disciplinaSelecionada.equals(disciplina.getNome());
							if (coincideDocente && coincideDisciplina) {
								model.addRow(new Object[]{disciplina.getId(), disciplina.getNome(),
										docente, nomeCurso, disciplina.getAno() + "º ano"});
							}
						}
					});
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarDisciplina());
		associarDocente.addActionListener(event -> associarDocente(table));
		removerDocente.addActionListener(event -> removerDocente(table));
		remover.addActionListener(event -> removerSelecionado(
				table, "disciplina", disciplinaController::remover));
		filtroDocente.addActionListener(event -> {
			if (!atualizandoFiltros[0]) reload.run();
		});
		filtroDisciplina.addActionListener(event -> {
			if (!atualizandoFiltros[0]) reload.run();
		});
		panel.putClientProperty("reload", reload);
		adicionarPainel("Disciplinas", panel);
	}

	/** Adiciona a seccao com a lista de docentes. */
	private void adicionarDocentes() {
		adicionarTabelaComRemocao("Docentes", "docente",
				new String[]{"ID", "Nome", "E-mail", "Nivel academico"}, docenteController,
				docente -> new Object[]{docente.getId(), docente.getNomeCompleto(), docente.getEmail(),
						docente.getNivelAcademico()}, this::criarDocente, docenteController::deletarDocente);
	}

	/** Apresenta estudantes ordenados alfabeticamente pelo nome. */
	private void adicionarEstudantes() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Nome", "E-mail", "Curso", "Ano"});
		JTable table = new JTable(model);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);
		sorter.toggleSortOrder(1);
		JPanel panel = criarPainelTabela("Estudantes por ordem alfabetica", table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> executarAtualizacao(atualizar, estudanteController::listar, estudantes -> {
			model.setRowCount(0);
			for (Estudante estudante : estudantes) {
				model.addRow(new Object[]{estudante.getId(), estudante.getNomeCompleto(), estudante.getEmail(),
						estudante.getCursoNome(), estudante.getAno()});
			}
			sorter.setSortKeys(java.util.Collections.singletonList(
					new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING)));
		});
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarEstudante());
		panel.putClientProperty("reload", reload);
		adicionarPainel("Estudantes", panel);
	}

	/** Adiciona a seccao com a lista de secretarios. */
	private void adicionarSecretarios() {
		adicionarTabelaComRemocao("Secretarios", "secretario",
				new String[]{"ID", "Nome", "E-mail", "Cargo"}, secretarioController,
				secretario -> new Object[]{secretario.getId(), secretario.getNomeCompleto(),
						secretario.getEmail(), secretario.getCargo()}, this::criarSecretario,
				secretarioController::deletarSecretario);
	}

	/** Adiciona a seccao com as turmas e os respetivos cursos. */
	private void adicionarTurmas() {
		adicionarTabela("Turmas", new String[]{"ID", "Chave", "Curso", "Ano"}, turmaController,
				turma -> new Object[]{turma.getId(), turma.getChave(), turma.getCursoNome(), turma.getAno()},
				this::criarTurma);
	}

	/** Monta a tabela de horarios com filtro por curso. */
	private void adicionarHorarios() {
		JComboBox<Opcao<Curso>> filtroCurso = opcoes(cursoController.listar(),
				Curso::getNome, "Todos os cursos");
		boolean[] atualizandoFiltro = {false};
		JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		filtros.setOpaque(false);
		filtros.add(new JLabel("Curso:"));
		filtros.add(filtroCurso);
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 24, 24));
		JLabel titulo = new JLabel("Horarios por curso e ano");
		titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
		titulo.setForeground(PaletaCores.TEXTO);
		JPanel cabecalho = new JPanel(new BorderLayout(0, 10));
		cabecalho.setOpaque(false);
		cabecalho.add(titulo, BorderLayout.NORTH);
		cabecalho.add(filtros, BorderLayout.CENTER);
		panel.add(cabecalho, BorderLayout.NORTH);
		JPanel grupos = new JPanel(new BorderLayout());
		grupos.setOpaque(false);
		panel.add(new JScrollPane(grupos), BorderLayout.CENTER);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		JButton[] acoesHorario = HorarioGestaoUI.criarBotoes(
				this, horarioController, () -> {
					Curso cursoSelecionado = valorSelecionado(filtroCurso);
					List<Horario> horarios = horarioController.listar();
					if (cursoSelecionado == null) return horarios;
					List<Horario> filtrados = new ArrayList<>();
					for (Horario horario : horarios) {
						if (horario.getCurso() != null
								&& horario.getCurso().getId() == cursoSelecionado.getId()) {
							filtrados.add(horario);
						}
					}
					return filtrados;
				}, salaController::listar);
		JPanel acoes = criarAcoes(atualizar, adicionar, acoesHorario[0], acoesHorario[1]);
		HorarioGestaoUI.ajustarLarguras(acoesHorario);
		panel.add(acoes, BorderLayout.SOUTH);
		Runnable reload = () -> {
			executarAtualizacao(atualizar,
					() -> new java.util.AbstractMap.SimpleEntry<>(
							cursoController.listar(), horarioController.listar()),
					dados -> {
						atualizarOpcoes(filtroCurso, dados.getKey(), Curso::getNome,
								"Todos os cursos", atualizandoFiltro);
						Curso cursoSelecionado = valorSelecionado(filtroCurso);
						List<Horario> filtrados = new ArrayList<>();
						for (Horario horario : dados.getValue()) {
							if (cursoSelecionado == null || horario.getCurso() != null
									&& horario.getCurso().getId() == cursoSelecionado.getId()) {
								filtrados.add(horario);
							}
						}
						grupos.removeAll();
						grupos.add(HorariosPorAnoUI.criar(filtrados, cursoSelecionado == null),
								BorderLayout.NORTH);
						grupos.revalidate();
						grupos.repaint();
					});
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarHorario());
		filtroCurso.addActionListener(event -> {
			if (!atualizandoFiltro[0]) reload.run();
		});
		panel.putClientProperty("reload", reload);
		adicionarPainel("Horarios", panel);
	}

	/** Apresenta as inscricoes registadas no sistema. */
	private void adicionarInscricoes() {
		adicionarTabela("Inscricoes", new String[]{"ID", "Estudante", "Disciplina", "Ano"}, inscricaoController,
				inscricao -> new Object[]{inscricao.getId(),
						inscricao.getEstudante() == null ? "-" : inscricao.getEstudante().getNomeCompleto(),
						inscricao.getDisciplina() == null ? "-" : inscricao.getDisciplina().getNome(),
						inscricao.getDisciplina() == null ? "-" : inscricao.getDisciplina().getAno() + "º ano"}, null);
	}

	/** Recolhe os dados e regista uma sala num departamento. */
	private void criarSala() {
		JTextField nome = new JTextField(20);
		Validacao.filtrarTexto(nome, 100);
		JComboBox<Opcao<Departamento>> departamento = opcoes(departamentoController.listar(),
				Departamento::getNome, null);
		if (departamento.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Crie um departamento antes de registar salas.",
					"Sem departamentos", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		mostrarFormulario("Nova sala", formulario("Nome", nome, "Departamento", departamento), () -> {
			try {
				Validacao.validarNomeSala(nome.getText());
			} catch (IllegalArgumentException e) {
				mostrarErro("Informe um nome de sala valido.");
				return false;
			}
			Departamento departamentoSelecionado = valorSelecionado(departamento);
			if (departamentoSelecionado == null) {
				mostrarErro("Selecione o departamento responsavel pela sala.");
				return false;
			}
			String nomeSala = nome.getText().trim();
			return executarOperacao("A guardar sala...", () -> salaController.salvar(
					new Sala(salaController.gerarProximoId(), nomeSala, departamentoSelecionado)),
					"Sala registada.");
		});
	}

	/** Move a sala selecionada para outro departamento. */
	private void alterarDepartamentoSala(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) {
			mostrarErro("Selecione uma sala primeiro.");
			return;
		}
		int linhaModelo = tabela.convertRowIndexToModel(linha);
		int salaId = Integer.parseInt(tabela.getModel().getValueAt(linhaModelo, 0).toString());
		JComboBox<Opcao<Departamento>> departamento = opcoes(departamentoController.listar(),
				Departamento::getNome, null);
		if (departamento.getItemCount() == 0) {
			mostrarErro("Registe um departamento antes de alterar a sala.");
			return;
		}
		mostrarFormulario("Alterar departamento da sala",
				formulario("Novo departamento", departamento), () -> {
			Departamento selecionado = valorSelecionado(departamento);
			if (selecionado == null) {
				throw new IllegalArgumentException("Selecione o novo departamento da sala.");
			}
			return executarOperacao("A alterar departamento da sala...", () -> {
				if (!salaController.alterarDepartamento(salaId, selecionado.getId())) {
					throw new IllegalStateException("A sala selecionada ja nao existe.");
				}
			}, "Departamento da sala alterado.");
		});
	}

	/** Remove a sala selecionada quando nao existem horarios ou reservas associados. */
	private void eliminarSala(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) {
			mostrarErro("Selecione uma sala primeiro.");
			return;
		}
		int linhaModelo = tabela.convertRowIndexToModel(linha);
		int salaId = Integer.parseInt(tabela.getModel().getValueAt(linhaModelo, 0).toString());
		String nomeSala = tabela.getModel().getValueAt(linhaModelo, 1).toString();
		int confirmacao = JOptionPane.showConfirmDialog(this,
				"Tem a certeza de que deseja eliminar a sala \"" + nomeSala + "\"?",
				"Confirmar eliminacao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirmacao != JOptionPane.YES_OPTION) return;
		executarOperacao("A eliminar sala...", () -> {
			if (!salaController.remover(salaId)) {
				throw new IllegalStateException("A sala selecionada ja nao existe.");
			}
		}, "Sala eliminada.");
	}

	/** Recolhe os dados e regista um curso num departamento. */
	private void criarCurso() {
		JTextField nome = new JTextField(20);
		Validacao.filtrarTexto(nome, 150);
		JComboBox<Opcao<Departamento>> departamento = opcoes(departamentoController.listar(),
				Departamento::getNome, null);
		if (departamento.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Crie um departamento antes de registar cursos.",
					"Sem departamentos", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		mostrarFormulario("Novo curso", formulario("Nome", nome, "Departamento", departamento), () -> {
			if (!validarTextoObrigatorio(nome, "Informe o nome do curso.", 150)) return false;
			Departamento departamentoSelecionado = valorSelecionado(departamento);
			if (departamentoSelecionado == null) {
				mostrarErro("Selecione o departamento responsavel pelo curso.");
				return false;
			}
			String nomeCurso = nome.getText().trim();
			return executarOperacao("A guardar curso...", () -> cursoController.salvar(
					new Curso(cursoController.gerarProximoId(), nomeCurso, departamentoSelecionado)),
					"Curso registado.");
		});
	}

	/** Cria um departamento sem exigir salas ou secretario. */
	private void criarDepartamento() {
		JTextField nome = new JTextField(20);
		Validacao.filtrarTexto(nome, 150);
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, "Sem curso");
		JComboBox<Opcao<Secretario>> secretario = opcoes(secretariosDisponiveis(null),
				Secretario::getNomeCompleto, "Sem secretario");
		mostrarFormulario("Novo departamento", formulario("Nome", nome, "Curso", curso,
				"Secretario responsavel (opcional)", secretario), () -> {
			if (!validarTextoObrigatorio(nome, "Informe o nome do departamento.", 150)) return false;
			Secretario responsavel = valorSelecionado(secretario);
			Curso cursoSelecionado = valorSelecionado(curso);
			String nomeDepartamento = nome.getText().trim();
			return executarOperacao("A guardar departamento...", () -> departamentoController.salvar(
					new Departamento(departamentoController.gerarProximoId(), nomeDepartamento,
							cursoSelecionado, responsavel)),
					"Departamento registado.");
		});
	}

	/** Associa ao departamento selecionado um secretario ainda disponivel. */
	private void atribuirSecretario(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) {
			JOptionPane.showMessageDialog(this, "Selecione um departamento primeiro.",
					"Departamento nao selecionado", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int departamentoId = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
		JComboBox<Opcao<Secretario>> secretario = opcoes(secretariosDisponiveis(departamentoId),
				Secretario::getNomeCompleto, "Sem secretario");
		JPanel form = formulario("Secretario responsavel", secretario);
		mostrarFormulario("Atribuir ao departamento", form, () -> {
			Secretario selecionado = valorSelecionado(secretario);
			Integer secretarioId = selecionado == null ? null : selecionado.getId();
			return executarOperacao("A atualizar secretario...", () -> {
				if (!departamentoController.atribuirSecretario(departamentoId, secretarioId)) {
					throw new IllegalStateException("Nao foi possivel atualizar o secretario do departamento. "
							+ "O secretario pode ja pertencer a outro departamento.");
				}
			}, selecionado == null ? "Secretario removido do departamento."
					: "Secretario atribuido ao departamento.");
		});
	}

	/** Retorna secretarios sem departamento, preservando a atribuicao atual indicada. */
	private List<Secretario> secretariosDisponiveis(Integer departamentoAtualId) {
		List<Integer> ocupados = new ArrayList<>();
		for (Departamento departamento : departamentoController.listar()) {
			if (departamento.getSecretario() != null
					&& (departamentoAtualId == null || departamento.getId() != departamentoAtualId)) {
				ocupados.add(departamento.getSecretario().getId());
			}
		}
		List<Secretario> disponiveis = new ArrayList<>();
		for (Secretario secretario : secretarioController.listar()) {
			if (!ocupados.contains(secretario.getId())) disponiveis.add(secretario);
		}
		return disponiveis;
	}

	/** Recolhe os dados e regista uma disciplina geral ou de um curso. */
	private void criarDisciplina() {
		JTextField nome = new JTextField(20);
		Validacao.filtrarTexto(nome, 150);
		ButtonGroup ano = new ButtonGroup();
		JPanel opcoesAno = criarOpcoesAno(ano);
		JComboBox<Opcao<Docente>> docente = opcoes(docenteController.listar(),
				Docente::getNomeCompleto, "Sem docente");
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome,
				"Disciplina geral (cadeira comum)");
		mostrarFormulario("Nova disciplina", formulario("Nome", nome, "Ano", opcoesAno,
				"Docente", docente, "Tipo / Curso", curso), () -> {
			if (!validarTextoObrigatorio(nome, "Informe o nome da disciplina.", 150)) return false;
			String nomeDisciplina = nome.getText().trim();
			int anoDisciplina = anoSelecionado(ano);
			Docente docenteSelecionado = valorSelecionado(docente);
			Curso cursoSelecionado = valorSelecionado(curso);
			return executarOperacao("A guardar disciplina...", () -> disciplinaController.salvar(
					new Disciplina(disciplinaController.gerarProximoId(), nomeDisciplina,
							docenteSelecionado, cursoSelecionado, anoDisciplina)), "Disciplina registada.");
		});
	}

	/** Associa um docente, ou remove a associacao, da disciplina selecionada. */
	private void associarDocente(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) {
			JOptionPane.showMessageDialog(this, "Selecione uma disciplina primeiro.",
					"Disciplina nao selecionada", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int linhaModelo = tabela.convertRowIndexToModel(linha);
		int disciplinaId = Integer.parseInt(tabela.getModel().getValueAt(linhaModelo, 0).toString());
		JComboBox<Opcao<Docente>> docente = opcoes(docenteController.listar(),
				Docente::getNomeCompleto, "Sem docente");
		mostrarFormulario("Associar docente a disciplina", formulario("Docente", docente), () -> {
			Docente selecionado = valorSelecionado(docente);
			Integer docenteId = selecionado == null ? null : selecionado.getId();
			return executarOperacao("A atualizar docente...", () -> {
				if (!disciplinaController.atribuirDocente(disciplinaId, docenteId)) {
					throw new IllegalStateException("Nao foi possivel atualizar o docente da disciplina.");
				}
			}, selecionado == null ? "Docente removido da disciplina."
					: "Docente associado a disciplina.");
		});
	}

	/** Remove o docente da disciplina selecionada sem remover a disciplina. */
	private void removerDocente(JTable tabela) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) return;
		int linhaModelo = tabela.convertRowIndexToModel(linha);
		if ("Sem docente".equals(tabela.getModel().getValueAt(linhaModelo, 2).toString())) {
			mostrarInformacao("A disciplina selecionada ja nao tem docente associado.");
			return;
		}
		int disciplinaId = Integer.parseInt(tabela.getModel()
				.getValueAt(linhaModelo, 0).toString());
		executarOperacao("A remover docente...", () -> {
			if (!disciplinaController.atribuirDocente(disciplinaId, null)) {
				throw new IllegalStateException("Nao foi possivel remover o docente da disciplina.");
			}
		}, "Docente removido da disciplina.");
	}

	/** Recolhe os dados e regista um novo docente. */
	private void criarDocente() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		ButtonGroup nivel = new ButtonGroup();
		JPanel opcoesNivel = criarOpcoesRadio(nivel,
				"Licenciatura", "Mestrado", "Doutorado", "Phd");
		JTextField celular = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarDigitos(celular, 9);
		mostrarFormulario("Novo docente", formulario(
				"Nome", nome, "Apelido", apelido, "Nivel academico", opcoesNivel,
				"Celular", celular, "Senha", campoSenha(senha)), () -> {
			String email = emGen.gerarEmail(nome, apelido);
			validarDadosUsuario(nome, apelido, celular, email, senha);
			String nomeDocente = nome.getText().trim();
			String apelidoDocente = apelido.getText().trim();
			String nivelDocente = radioSelecionado(nivel);
			int celularDocente = Validacao.validarCelular(celular.getText());
			String emailDocente = Validacao.validarEnderecoEmail(email);
			String senhaDocente = new String(senha.getPassword());
			return executarOperacao("A guardar docente...", () -> docenteController.salvar(new Docente(
					docenteController.gerarProximoId(), nomeDocente, apelidoDocente,
					nivelDocente, celularDocente, emailDocente, senhaDocente)),
					"Docente registado.");
		});
	}

	/** Recolhe os dados e regista um estudante associado a um curso. */
	private void criarEstudante() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, null);
		ButtonGroup ano = new ButtonGroup();
		JPanel opcoesAno = criarOpcoesAno(ano);
		JTextField celular = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarDigitos(celular, 9);
		mostrarFormulario("Novo estudante", formulario(
				"Nome", nome, "Apelido", apelido, "Curso", curso, "Ano", opcoesAno,
				"Celular", celular, "Senha", campoSenha(senha)), () -> {
			String email = emGen.gerarEmail(nome, apelido);
			validarDadosUsuario(nome, apelido, celular, email, senha);
			Curso cursoSelecionado = valorSelecionado(curso);
			if (cursoSelecionado == null) {
				throw new IllegalArgumentException("Selecione um curso para o estudante.");
			}
			int anoEstudo = anoSelecionado(ano);
			String nomeEstudante = nome.getText().trim();
			String apelidoEstudante = apelido.getText().trim();
			int celularEstudante = Validacao.validarCelular(celular.getText());
			String emailEstudante = Validacao.validarEnderecoEmail(email);
			String senhaEstudante = new String(senha.getPassword());
			return executarOperacao("A guardar estudante...", () -> {
				Estudante estudante = new Estudante(estudanteController.gerarProximoId(),
						nomeEstudante, apelidoEstudante, cursoSelecionado, anoEstudo,
						celularEstudante, emailEstudante, senhaEstudante);
				if (!estudanteController.cadastrarEstudante(estudante)) {
					throw new IllegalStateException("Nao foi possivel registar o estudante. "
							+ "Verifique se ja existe um registo duplicado.");
				}
			}, "Estudante registado.");
		});
	}

	/** Recolhe os dados e regista um novo secretario. */
	private void criarSecretario() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		JTextField cargo = new JTextField("Secretário", 20);
		JTextField celular = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarTexto(cargo, 100);
		Validacao.filtrarDigitos(celular, 9);
		mostrarFormulario("Novo secretario", formulario(
				"Nome", nome, "Apelido", apelido, "Cargo", cargo,
				"Celular", celular, "Senha", campoSenha(senha)), () -> {
			String email= emGen.gerarEmail(nome, apelido);
			validarDadosUsuario(nome, apelido, celular, email, senha);
			Validacao.validarTextoObrigatorio(cargo.getText(), "cargo", 100);
			String nomeSecretario = nome.getText().trim();
			String apelidoSecretario = apelido.getText().trim();
			String cargoSecretario = cargo.getText().trim();
			int celularSecretario = Validacao.validarCelular(celular.getText());
			String emailSecretario = Validacao.validarEnderecoEmail(email);
			String senhaSecretario = new String(senha.getPassword());
			return executarOperacao("A guardar secretario...", () -> secretarioController.salvar(new Secretario(
					secretarioController.gerarProximoId(), nomeSecretario, apelidoSecretario,
					cargoSecretario, celularSecretario, emailSecretario, senhaSecretario)),
					"Secretario registado.");
		});
	}

	/** Cria uma turma e permite associar varios estudantes do curso. */
	private void criarTurma() {
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, null);
		JTextField ano = new JTextField(20);
		Validacao.filtrarDigitos(ano, 4);
		DefaultListModel<Opcao<Estudante>> estudantesModel = new DefaultListModel<>();
		for (Estudante estudante : estudanteController.listar()) {
			estudantesModel.addElement(new Opcao<>(estudante, estudante.getNomeCompleto()
					+ " - " + estudante.getCursoNome() + " (" + estudante.getAno() + ")"));
		}
		JList<Opcao<Estudante>> estudantes = new JList<>(estudantesModel);
		estudantes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane listaEstudantes = new JScrollPane(estudantes);
		listaEstudantes.setPreferredSize(new java.awt.Dimension(260, 120));
		mostrarFormulario("Nova turma", formulario(
				"Curso", curso, "Ano", ano, "Estudantes (selecao multipla)", listaEstudantes), () -> {
			Curso cursoSelecionado = valorSelecionado(curso);
			if (cursoSelecionado == null) {
				throw new IllegalArgumentException("Selecione um curso para a turma.");
			}
			int anoTurma = Validacao.validarInteiro(ano.getText(), "ano", 1, 9999);
			String chave = anoTurma + "-" + cursoSelecionado.getNome();
			if (turmaController.existePorChave(chave)) {
				throw new IllegalArgumentException("Ja existe uma turma para este curso e ano.");
			}
			List<Estudante> selecionados = new ArrayList<>();
			for (Opcao<Estudante> opcao : estudantes.getSelectedValuesList()) {
				selecionados.add(valorOpcao(opcao));
			}
			return executarOperacao("A guardar turma...", () -> turmaController.salvar(new Turma(
					turmaController.gerarProximoId(), anoTurma, cursoSelecionado, selecionados)),
					"Turma registada.");
		});
	}

	/** Recolhe curso, disciplina, sala, dia e horas para criar um horario. */
	private void criarHorario() {
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, null);
		JComboBox<Opcao<Disciplina>> disciplina = new JComboBox<>();
		atualizarDisciplinasDoCurso(disciplina, valorSelecionado(curso));
		curso.addActionListener(event ->
				atualizarDisciplinasDoCurso(disciplina, valorSelecionado(curso)));
		JComboBox<Opcao<Sala>> sala = opcoes(salaController.listar(), Sala::getNome, null);
		if (sala.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Registe uma sala antes de criar um horario.",
					"Sem salas", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		HorarioGestaoUI.SeletorDia dia = HorarioGestaoUI.criarSeletorDia(DiaSemana.SEGUNDA);
		JTextField inicio = new JTextField("08:00", 20);
		JTextField fim = new JTextField("09:00", 20);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		mostrarFormulario("Novo horario", formulario(
				"Curso", curso, "Disciplina", disciplina, "Sala", sala, "Dia", dia.componente(),
				"Hora de inicio (HH:MM)", inicio, "Hora de fim (HH:MM)", fim), () -> {
			Curso cursoSelecionado = valorSelecionado(curso);
			if (cursoSelecionado == null) {
				throw new IllegalArgumentException("Selecione um curso para o horario.");
			}
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			Disciplina disciplinaSelecionada = valorSelecionado(disciplina);
			Sala salaSelecionada = valorSelecionado(sala);
			DiaSemana diaSelecionado = dia.diaSelecionado();
			return executarOperacao("A guardar horario...", () -> horarioController.salvar(new Horario(
					horarioController.gerarProximoId(), cursoSelecionado, disciplinaSelecionada,
					salaSelecionada, diaSelecionado, horaInicio, horaFim)), "Horario registado.");
		});
	}

	/** Organiza rotulos e componentes num formulario em duas colunas. */
	private JPanel formulario(Object... campos) {
		JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
		form.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
		for (int i = 0; i < campos.length; i += 2) {
			form.add(new javax.swing.JLabel((String) campos[i]));
			form.add((Component) campos[i + 1]);
		}
		return form;
	}

	/** Cria opcoes de ano letivo de 1 a 5 para os formularios de registo. */
	private JPanel criarOpcoesAno(ButtonGroup grupo) {
		JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		painel.setOpaque(false);
		for (int ano = 1; ano <= 5; ano++) {
			JRadioButton opcao = new JRadioButton(ano + "º ano");
			opcao.setActionCommand(Integer.toString(ano));
			opcao.setOpaque(false);
			grupo.add(opcao);
			painel.add(opcao);
			if (ano == 1) opcao.setSelected(true);
		}
		return painel;
	}

	/** Cria um grupo de radio buttons e seleciona a primeira opcao por padrao. */
	private JPanel criarOpcoesRadio(ButtonGroup grupo, String... opcoes) {
		JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		painel.setOpaque(false);
		for (int indice = 0; indice < opcoes.length; indice++) {
			JRadioButton opcao = new JRadioButton(opcoes[indice]);
			opcao.setActionCommand(opcoes[indice]);
			opcao.setOpaque(false);
			grupo.add(opcao);
			painel.add(opcao);
			if (indice == 0) opcao.setSelected(true);
		}
		return painel;
	}

	private int anoSelecionado(ButtonGroup grupo) {
		return Integer.parseInt(radioSelecionado(grupo));
	}

	private String radioSelecionado(ButtonGroup grupo) {
		if (grupo.getSelection() == null) {
			throw new IllegalArgumentException("Selecione uma opcao.");
		}
		return grupo.getSelection().getActionCommand();
	}

	/** Atualiza a lista de disciplinas conforme o curso selecionado. */
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

	private <T> void adicionarTabelaComRemocao(String titulo, String entidade, String[] colunas,
			Controller<T> controller, Function<T, Object[]> mapear, Runnable criar,
			Function<Integer, Boolean> remover) {
		DefaultTableModel model = modeloTabela(colunas);
		JTable table = new JTable(model);
		JPanel panel = criarPainelTabela(titulo, table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		JButton removerButton = new JButton("Remover");
		panel.add(criarAcoes(atualizar, adicionar, removerButton), BorderLayout.SOUTH);
		mostrarAcoesAoSelecionar(table, removerButton);
		Runnable reload = () -> executarAtualizacao(atualizar, controller::listar, items -> {
			model.setRowCount(0);
			for (T item : items) {
				model.addRow(mapear.apply(item));
			}
		});
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criar.run());
		removerButton.addActionListener(event ->
				removerSelecionado(table, entidade, remover));
		panel.putClientProperty("reload", reload);
		adicionarPainel(titulo, panel);
	}

	private void removerSelecionado(JTable tabela, String entidade,
			Function<Integer, Boolean> remover) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) return;
		int linhaModelo = tabela.convertRowIndexToModel(linha);
		int id = Integer.parseInt(tabela.getModel().getValueAt(linhaModelo, 0).toString());
		String nome = tabela.getModel().getColumnCount() > 1
				? tabela.getModel().getValueAt(linhaModelo, 1).toString()
				: Integer.toString(id);
		int resposta = JOptionPane.showConfirmDialog(this,
				"Tem a certeza de que deseja remover " + entidade + " \"" + nome + "\"?",
				"Confirmar remocao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (resposta != JOptionPane.YES_OPTION) return;
		executarOperacao("A remover " + entidade + "...", () -> {
			if (!remover.apply(id)) {
				throw new IllegalStateException("O registo selecionado ja nao existe.");
			}
		}, entidade.substring(0, 1).toUpperCase() + entidade.substring(1) + " removido.");
	}

	/** Recarrega um seletor de objetos e preserva a opcao anteriormente selecionada. */
	private <T> void atualizarOpcoes(JComboBox<Opcao<T>> combo, List<T> valores,
			Function<T, String> descricao, String opcaoVazia, boolean[] atualizando) {
		String selecionada = combo.getSelectedItem() == null
				? null : combo.getSelectedItem().toString();
		atualizando[0] = true;
		combo.removeAllItems();
		combo.setEnabled(true);
		combo.addItem(new Opcao<>(null, opcaoVazia));
		for (T valor : valores) {
			combo.addItem(new Opcao<>(valor, descricao.apply(valor)));
		}
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (selecionada != null && selecionada.equals(combo.getItemAt(i).toString())) {
				combo.setSelectedIndex(i);
				break;
			}
		}
		atualizando[0] = false;
	}

	/** Recarrega um seletor de texto mantendo a selecao quando possivel. */
	private void atualizarOpcoesTexto(JComboBox<String> combo, List<String> valores,
			String opcaoVazia, boolean incluirSemDocente, boolean[] atualizando) {
		String selecionada = (String) combo.getSelectedItem();
		atualizando[0] = true;
		combo.removeAllItems();
		combo.setEnabled(true);
		combo.addItem(opcaoVazia);
		if (incluirSemDocente) combo.addItem("Sem docente");
		for (String valor : valores) combo.addItem(valor);
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (selecionada != null && selecionada.equals(combo.getItemAt(i))) {
				combo.setSelectedIndex(i);
				break;
			}
		}
		atualizando[0] = false;
	}

	/** Combina rotulo e filtro com a tabela da seccao. */
	private JPanel painelComFiltro(String titulo, String rotulo, Component filtro, JTable tabela) {
		JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		filtros.setOpaque(false);
		filtros.add(new JLabel(rotulo));
		filtros.add(filtro);
		return painelPersonalizado(titulo, filtros, tabela);
	}

	/** Cria o painel de seccao com cabecalho, filtros e tabela. */
	private JPanel painelPersonalizado(String titulo, JPanel filtros, JTable tabela) {
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(PaletaCores.FUNDO);
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 24, 24));
		JLabel heading = new JLabel(titulo);
		heading.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
		heading.setForeground(PaletaCores.TEXTO);
		JPanel top = new JPanel(new BorderLayout(0, 10));
		top.setOpaque(false);
		top.add(heading, BorderLayout.NORTH);
		top.add(filtros, BorderLayout.CENTER);
		panel.add(top, BorderLayout.NORTH);
		tabela.setRowHeight(30);
		tabela.setFillsViewportHeight(true);
		panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
		return panel;
	}

	/** Cria um seletor de objetos e inclui uma opcao vazia quando solicitada. */
	private <T> JComboBox<Opcao<T>> opcoes(List<T> valores, Function<T, String> rotulo,
			String opcaoVazia) {
		JComboBox<Opcao<T>> combo = comboOpcoes(valores, rotulo);
		if (opcaoVazia != null) {
			combo.insertItemAt(new Opcao<>(null, opcaoVazia), 0);
			combo.setSelectedIndex(0);
		} else if (combo.getItemCount() == 0) {
			combo.setEnabled(false);
		}
		return combo;
	}

	/** Valida os campos comuns usados no registo de utilizadores. */
	private void validarDadosUsuario(JTextField nome, JTextField apelido, JTextField celular,
			String email, JPasswordField senha) {
		Validacao.validarNomePessoa(nome.getText(), "Nome");
		Validacao.validarNomePessoa(apelido.getText(), "Apelido");
		Validacao.validarCelular(celular.getText());
		Validacao.validarEnderecoEmail(email);
		if (senha.getPassword().length < 8) {
			throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
		}
	}

	/** Valida o texto e o tamanho maximo antes de registar dados. */
	private boolean validarTextoObrigatorio(JTextField campo, String mensagem, int limite) {
		try {
			Validacao.validarTextoObrigatorio(campo.getText(), "campo", limite);
		} catch (IllegalArgumentException e) {
			mostrarErro(mensagem);
			return false;
		}
		return true;
	}

}
