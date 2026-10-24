package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
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
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Secretario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Turma;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> {
			atualizarOpcoes(filtro, departamentoController.listar(), Departamento::getNome,
					"Todos os departamentos", atualizandoFiltro);
			Departamento selecionado = valorSelecionado(filtro);
			model.setRowCount(0);
			for (Sala sala : salaController.listar()) {
				if (selecionado == null || sala.getDepartamento() != null
						&& sala.getDepartamento().getId() == selecionado.getId()) {
					model.addRow(new Object[]{sala.getId(), sala.getNome(), sala.getEstado(),
							sala.getDepartamento() == null ? "-" : sala.getDepartamento().getNome()});
				}
			}
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarSala());
		filtro.addActionListener(event -> {
			if (!atualizandoFiltro[0]) reload.run();
		});
		panel.putClientProperty("reload", reload);
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
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Nome", "Secretario"});
		JTable table = new JTable(model);
		JPanel panel = criarPainelTabela("Departamentos e secretarios", table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar departamento");
		JButton atribuir = new JButton("Atribuir secretario");
		panel.add(criarAcoes(atualizar, adicionar, atribuir), BorderLayout.SOUTH);
		Runnable reload = () -> {
			model.setRowCount(0);
			for (Departamento departamento : departamentoController.listar()) {
				model.addRow(new Object[]{departamento.getId(), departamento.getNome(),
						departamento.getSecretario() == null ? "Por atribuir"
								: departamento.getSecretario().getNomeCompleto()});
			}
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarDepartamento());
		atribuir.addActionListener(event -> atribuirSecretario(table, reload));
		panel.putClientProperty("reload", reload);
		adicionarPainel("Departamentos", panel);
	}

	/** Monta filtros para consultar disciplinas por docente e disciplina. */
	private void adicionarDisciplinas() {
		DefaultTableModel model = modeloTabela(new String[]{"ID", "Disciplina", "Docente", "Tipo / Curso"});
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
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> {
			List<String> docentes = new ArrayList<>();
			for (Docente docente : docenteController.listar()) docentes.add(docente.getNomeCompleto());
			List<String> disciplinas = new ArrayList<>();
			for (Disciplina disciplina : disciplinaController.listar()) disciplinas.add(disciplina.getNome());
			atualizarOpcoesTexto(filtroDocente, docentes, "Todos os docentes", true, atualizandoFiltros);
			atualizarOpcoesTexto(filtroDisciplina, disciplinas, "Todas as disciplinas", false,
					atualizandoFiltros);
			String docenteSelecionado = (String) filtroDocente.getSelectedItem();
			String disciplinaSelecionada = (String) filtroDisciplina.getSelectedItem();
			model.setRowCount(0);
			for (Disciplina disciplina : disciplinaController.listar()) {
				String docente = disciplina.getDocente() == null
						? "Sem docente" : disciplina.getDocente().getNomeCompleto();
				String nomeCurso = disciplina.getCurso() == null || disciplina.getCurso().getId() <= 0
						? "Disciplina geral" : disciplina.getCurso().getNome();
				boolean coincideDocente = docenteSelecionado == null
						|| "Todos os docentes".equals(docenteSelecionado)
						|| docenteSelecionado.equals(docente);
				boolean coincideDisciplina = disciplinaSelecionada == null
						|| "Todas as disciplinas".equals(disciplinaSelecionada)
						|| disciplinaSelecionada.equals(disciplina.getNome());
				if (coincideDocente && coincideDisciplina) {
					model.addRow(new Object[]{disciplina.getId(), disciplina.getNome(), docente, nomeCurso});
				}
			}
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarDisciplina());
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
		adicionarTabela("Docentes", new String[]{"ID", "Nome", "E-mail", "Nivel academico"}, docenteController,
				docente -> new Object[]{docente.getId(), docente.getNomeCompleto(), docente.getEmail(),
						docente.getNivelAcademico()}, this::criarDocente);
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
		Runnable reload = () -> {
			model.setRowCount(0);
			for (Estudante estudante : estudanteController.listar()) {
				model.addRow(new Object[]{estudante.getId(), estudante.getNomeCompleto(), estudante.getEmail(),
						estudante.getCursoNome(), estudante.getAno()});
			}
			sorter.setSortKeys(java.util.Collections.singletonList(
					new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING)));
		};
		atualizar.addActionListener(event -> reload.run());
		adicionar.addActionListener(event -> criarEstudante());
		panel.putClientProperty("reload", reload);
		adicionarPainel("Estudantes", panel);
	}

	/** Adiciona a seccao com a lista de secretarios. */
	private void adicionarSecretarios() {
		adicionarTabela("Secretarios", new String[]{"ID", "Nome", "E-mail", "Cargo"}, secretarioController,
				secretario -> new Object[]{secretario.getId(), secretario.getNomeCompleto(),
						secretario.getEmail(), secretario.getCargo()}, this::criarSecretario);
	}

	/** Adiciona a seccao com as turmas e os respetivos cursos. */
	private void adicionarTurmas() {
		adicionarTabela("Turmas", new String[]{"ID", "Chave", "Curso", "Ano"}, turmaController,
				turma -> new Object[]{turma.getId(), turma.getChave(), turma.getCursoNome(), turma.getAno()},
				this::criarTurma);
	}

	/** Monta a tabela de horarios com filtro por curso. */
	private void adicionarHorarios() {
		DefaultTableModel model = modeloTabela(
				new String[]{"ID", "Dia", "Inicio", "Fim", "Curso", "Disciplina", "Sala"});
		JTable table = new JTable(model);
		JComboBox<Opcao<Curso>> filtroCurso = opcoes(cursoController.listar(),
				Curso::getNome, "Todos os cursos");
		boolean[] atualizandoFiltro = {false};
		JPanel panel = painelComFiltro("Horarios", "Curso:", filtroCurso, table);
		JButton atualizar = new JButton("Atualizar");
		JButton adicionar = new JButton("Adicionar");
		panel.add(criarAcoes(atualizar, adicionar), BorderLayout.SOUTH);
		Runnable reload = () -> {
			atualizarOpcoes(filtroCurso, cursoController.listar(), Curso::getNome,
					"Todos os cursos", atualizandoFiltro);
			Curso cursoSelecionado = valorSelecionado(filtroCurso);
			model.setRowCount(0);
			for (Horario horario : horarioController.listar()) {
				if (cursoSelecionado == null || horario.getCurso() != null
						&& horario.getCurso().getId() == cursoSelecionado.getId()) {
					model.addRow(new Object[]{horario.getId(), horario.getDiaSemana(),
							horario.getHoraInicio(), horario.getHoraFim(), horario.getNomeCurso(),
							horario.getDisciplina() == null ? "-" : horario.getDisciplina().getNome(),
							horario.getSala() == null ? "Por definir" : horario.getSala().getNome()});
				}
			}
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
		adicionarTabela("Inscricoes", new String[]{"ID", "Estudante", "Disciplina"}, inscricaoController,
				inscricao -> new Object[]{inscricao.getId(),
						inscricao.getEstudante() == null ? "-" : inscricao.getEstudante().getNomeCompleto(),
						inscricao.getDisciplina() == null ? "-" : inscricao.getDisciplina().getNome()}, null);
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
		if (!confirmarFormulario("Nova sala", formulario("Nome", nome, "Departamento", departamento))) {
			return;
		}
		if (!validarTextoObrigatorio(nome, "Informe o nome da sala.", 100)) return;
		Departamento departamentoSelecionado = valorSelecionado(departamento);
		if (departamentoSelecionado == null) {
			mostrarErro("Selecione o departamento responsavel pela sala.");
			return;
		}
		executarOperacao("A guardar sala...", () -> salaController.salvar(
				new Sala(salaController.gerarProximoId(), nome.getText().trim(),
						departamentoSelecionado)), "Sala registada.");
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
		if (!confirmarFormulario("Novo curso", formulario("Nome", nome, "Departamento", departamento))) {
			return;
		}
		if (!validarTextoObrigatorio(nome, "Informe o nome do curso.", 150)) return;
		Departamento departamentoSelecionado = valorSelecionado(departamento);
		if (departamentoSelecionado == null) {
			mostrarErro("Selecione o departamento responsavel pelo curso.");
			return;
		}
		executarOperacao("A guardar curso...", () -> cursoController.salvar(
				new Curso(cursoController.gerarProximoId(), nome.getText().trim(),
						departamentoSelecionado)), "Curso registado.");
	}

	/** Recolhe os dados e cria um departamento com secretario responsavel. */
	private void criarDepartamento() {
		JTextField nome = new JTextField(20);
		Validacao.filtrarTexto(nome, 150);
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, "Sem curso");
		JComboBox<Opcao<Sala>> sala = opcoes(salaController.listar(), Sala::getNome, "Sem sala");
		JComboBox<Opcao<Secretario>> secretario = opcoes(secretariosDisponiveis(null),
				Secretario::getNomeCompleto, null);
		if (secretario.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this,
					"Registe primeiro um secretario para o departamento.",
					"Sem secretarios", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (!confirmarFormulario("Novo departamento", formulario("Nome", nome, "Curso", curso,
				"Sala", sala, "Secretario responsavel", secretario))) {
			return;
		}
		if (!validarTextoObrigatorio(nome, "Informe o nome do departamento.", 150)) return;
		Secretario responsavel = valorSelecionado(secretario);
		if (responsavel == null) {
			mostrarErro("Selecione o secretario responsavel pelo departamento.");
			return;
		}
		executarOperacao("A guardar departamento...", () -> departamentoController.salvar(
				new Departamento(departamentoController.gerarProximoId(), nome.getText().trim(),
						valorSelecionado(curso), valorSelecionado(sala), responsavel)),
				"Departamento registado com secretario atribuido.");
	}

	/** Associa ao departamento selecionado um secretario ainda disponivel. */
	private void atribuirSecretario(JTable tabela, Runnable reload) {
		int linha = tabela.getSelectedRow();
		if (linha < 0) {
			JOptionPane.showMessageDialog(this, "Selecione um departamento primeiro.",
					"Departamento nao selecionado", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int departamentoId = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
		JComboBox<Opcao<Secretario>> secretario = opcoes(secretariosDisponiveis(departamentoId),
				Secretario::getNomeCompleto, null);
		if (secretario.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this, "Nao ha secretarios livres para atribuir.",
					"Sem secretarios", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JPanel form = formulario("Secretario responsavel", secretario);
		if (!confirmarFormulario("Atribuir ao departamento", form)) {
			return;
		}
		Secretario selecionado = valorSelecionado(secretario);
		if (selecionado != null && departamentoController.atribuirSecretario(departamentoId,
				selecionado.getId())) {
			JOptionPane.showMessageDialog(this, "Secretario atribuido ao departamento.");
			reload.run();
		} else {
			mostrarErro("Nao foi possivel atribuir o secretario. Ele pode ja pertencer a outro departamento.");
		}
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
		JComboBox<Opcao<Docente>> docente = opcoes(docenteController.listar(),
				Docente::getNomeCompleto, "Sem docente");
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome,
				"Disciplina geral (cadeira comum)");
		if (!confirmarFormulario("Nova disciplina", formulario("Nome", nome, "Docente", docente,
				"Tipo / Curso", curso))) {
			return;
		}
		if (!validarTextoObrigatorio(nome, "Informe o nome da disciplina.", 150)) return;
		executarOperacao("A guardar disciplina...", () -> disciplinaController.salvar(
				new Disciplina(disciplinaController.gerarProximoId(), nome.getText().trim(),
						valorSelecionado(docente), valorSelecionado(curso))), "Disciplina registada.");
	}

	/** Recolhe os dados e regista um novo docente. */
	private void criarDocente() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		JTextField nivel = new JTextField(20);
		JTextField celular = new JTextField(20);
		JTextField email = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarTexto(nivel, 100);
		Validacao.filtrarDigitos(celular, 9);
		if (!confirmarFormulario("Novo docente", formulario(
				"Nome", nome, "Apelido", apelido, "Nivel academico", nivel,
				"Celular", celular, "E-mail", email, "Senha", senha))) {
			return;
		}
		try {
			validarDadosUsuario(nome, apelido, celular, email, senha);
			Validacao.validarTextoObrigatorio(nivel.getText(), "nivel academico", 100);
			executarOperacao("A guardar docente...", () -> docenteController.salvar(new Docente(
					docenteController.gerarProximoId(), nome.getText().trim(), apelido.getText().trim(),
					nivel.getText().trim(), Validacao.validarCelular(celular.getText()),
					Validacao.validarEnderecoEmail(email.getText()), new String(senha.getPassword()))),
					"Docente registado.");
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
	}

	/** Recolhe os dados e regista um estudante associado a um curso. */
	private void criarEstudante() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		JComboBox<Opcao<Curso>> curso = opcoes(cursoController.listar(), Curso::getNome, null);
		JTextField ano = new JTextField(20);
		JTextField celular = new JTextField(20);
		JTextField email = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarDigitos(ano, 4);
		Validacao.filtrarDigitos(celular, 9);
		if (!confirmarFormulario("Novo estudante", formulario(
				"Nome", nome, "Apelido", apelido, "Curso", curso, "Ano", ano,
				"Celular", celular, "E-mail", email, "Senha", senha))) {
			return;
		}
		try {
			validarDadosUsuario(nome, apelido, celular, email, senha);
			Curso cursoSelecionado = valorSelecionado(curso);
			if (cursoSelecionado == null) {
				throw new IllegalArgumentException("Selecione um curso para o estudante.");
			}
			int anoEstudo = Validacao.validarInteiro(ano.getText(), "ano", 1, 9999);
			Estudante estudante = new Estudante(estudanteController.gerarProximoId(),
					nome.getText().trim(), apelido.getText().trim(), cursoSelecionado, anoEstudo,
					Validacao.validarCelular(celular.getText()),
					Validacao.validarEnderecoEmail(email.getText()),
					new String(senha.getPassword()));
			if (estudanteController.cadastrarEstudante(estudante)) {
				JOptionPane.showMessageDialog(this, "Estudante registado.");
				atualizarSelecionado();
			} else {
				mostrarErro("Nao foi possivel registar o estudante. Verifique os dados e se ja existe um registo duplicado.");
			}
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
	}

	/** Recolhe os dados e regista um novo secretario. */
	private void criarSecretario() {
		JTextField nome = new JTextField(20);
		JTextField apelido = new JTextField(20);
		JTextField cargo = new JTextField(20);
		JTextField celular = new JTextField(20);
		JTextField email = new JTextField(20);
		JPasswordField senha = new JPasswordField(20);
		Validacao.filtrarNomePessoa(nome);
		Validacao.filtrarNomePessoa(apelido);
		Validacao.filtrarTexto(cargo, 100);
		Validacao.filtrarDigitos(celular, 9);
		if (!confirmarFormulario("Novo secretario", formulario(
				"Nome", nome, "Apelido", apelido, "Cargo", cargo,
				"Celular", celular, "E-mail", email, "Senha", senha))) {
			return;
		}
		try {
			validarDadosUsuario(nome, apelido, celular, email, senha);
			Validacao.validarTextoObrigatorio(cargo.getText(), "cargo", 100);
			executarOperacao("A guardar secretario...", () -> secretarioController.salvar(new Secretario(
					secretarioController.gerarProximoId(), nome.getText().trim(), apelido.getText().trim(),
					cargo.getText().trim(), Validacao.validarCelular(celular.getText()),
					Validacao.validarEnderecoEmail(email.getText()), new String(senha.getPassword()))),
					"Secretario registado.");
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
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
		if (!confirmarFormulario("Nova turma", formulario(
				"Curso", curso, "Ano", ano, "Estudantes (selecao multipla)", listaEstudantes))) {
			return;
		}
		try {
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
			executarOperacao("A guardar turma...", () -> turmaController.salvar(new Turma(
					turmaController.gerarProximoId(), anoTurma, cursoSelecionado, selecionados)),
					"Turma registada.");
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
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
		JComboBox<DiaSemana> dia = new JComboBox<>(DiaSemana.values());
		JTextField inicio = new JTextField("08:00", 20);
		JTextField fim = new JTextField("09:00", 20);
		Validacao.filtrarDataHora(inicio, false);
		Validacao.filtrarDataHora(fim, false);
		if (!confirmarFormulario("Novo horario", formulario(
				"Curso", curso, "Disciplina", disciplina, "Sala", sala, "Dia", dia,
				"Hora de inicio (HH:MM)", inicio, "Hora de fim (HH:MM)", fim))) {
			return;
		}
		try {
			Curso cursoSelecionado = valorSelecionado(curso);
			if (cursoSelecionado == null) {
				throw new IllegalArgumentException("Selecione um curso para o horario.");
			}
			LocalTime horaInicio = Validacao.validarHora(inicio.getText(), "a hora de inicio");
			LocalTime horaFim = Validacao.validarHora(fim.getText(), "a hora de fim");
			if (!horaFim.isAfter(horaInicio)) {
				throw new IllegalArgumentException("A hora final deve ser posterior a hora inicial.");
			}
			executarOperacao("A guardar horario...", () -> horarioController.salvar(new Horario(
					horarioController.gerarProximoId(), cursoSelecionado, valorSelecionado(disciplina),
					valorSelecionado(sala),
					(DiaSemana) dia.getSelectedItem(), horaInicio, horaFim)), "Horario registado.");
		} catch (IllegalArgumentException e) {
			mostrarErro(e.getMessage());
		}
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

	/** Apresenta o formulario e devolve se o utilizador confirmou. */
	private boolean confirmarFormulario(String titulo, JPanel form) {
		return JOptionPane.showConfirmDialog(this, form, titulo,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
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
			JTextField email, JPasswordField senha) {
		Validacao.validarNomePessoa(nome.getText(), "Nome");
		Validacao.validarNomePessoa(apelido.getText(), "Apelido");
		Validacao.validarCelular(celular.getText());
		Validacao.validarEnderecoEmail(email.getText());
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
