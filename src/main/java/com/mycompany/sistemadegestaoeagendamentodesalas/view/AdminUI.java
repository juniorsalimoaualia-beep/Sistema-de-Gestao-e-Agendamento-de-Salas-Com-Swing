package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.Controller;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.InscricaoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.TurmaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Secretario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Turma;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class AdminUI extends JFrame {
	private static final Color INK = new Color(31, 49, 45);
	private static final Color PAPER = new Color(246, 247, 241);
	private final String perfil;
	private final String email;
	private final SalaController salaController = new SalaController();
	private final CursoController cursoController = new CursoController();
	private final DepartamentoController departamentoController = new DepartamentoController();
	private final DisciplinaController disciplinaController = new DisciplinaController();
	private final DocenteController docenteController = new DocenteController();
	private final EstudanteController estudanteController = new EstudanteController();
	private final HorarioController horarioController = new HorarioController();
	private final InscricaoController inscricaoController = new InscricaoController();
	private final ReservaController reservaController = new ReservaController();
	private final SecretarioController secretarioController = new SecretarioController();
	private final TurmaController turmaController = new TurmaController();
	private final JTabbedPane tabs = new JTabbedPane();

	public AdminUI(String perfil, String email) {
		super("SGSAS | Painel de " + nomePerfil(perfil));
		this.perfil = perfil;
		this.email = email;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(900, 600));
		setLocationRelativeTo(null);
		getContentPane().setBackground(PAPER);
		tabs.setTabPlacement(JTabbedPane.LEFT);
		tabs.setBackground(PAPER);
		add(criarCabecalho(), BorderLayout.NORTH);
		tabs.addTab("Inicio", criarInicio());
		if ("ADMIN".equalsIgnoreCase(perfil)) {
			adicionarSalas();
			adicionarCursos();
			adicionarDepartamentos();
			adicionarDisciplinas();
			adicionarDocentes();
			adicionarEstudantes();
			adicionarSecretarios();
			adicionarTurmas();
			adicionarHorarios();
			adicionarReservas();
			adicionarInscricoes();
		} else if ("SECRETARIO".equalsIgnoreCase(perfil)) {
			adicionarSalas();
			adicionarReservas();
			adicionarHorarios();
		} else if ("DOCENTE".equalsIgnoreCase(perfil)) {
			adicionarDisciplinas();
			adicionarHorarios();
			adicionarReservas();
		} else if ("ESTUDANTE".equalsIgnoreCase(perfil)) {
			adicionarHorarios();
			adicionarDisciplinas();
			adicionarInscricoes();
		} else {
			throw new IllegalArgumentException("Perfil desconhecido: " + perfil);
		}
		tabs.addTab("Conta", new UsuarioUI(perfil, email));
		add(tabs, BorderLayout.CENTER);
		tabs.setSelectedIndex(0);
		tabs.addChangeListener(event -> atualizarSelecionado());
	}

	private void adicionarSalas() {
		adicionarTabela("Salas", new String[]{"ID", "Nome", "Estado", "Departamento"}, salaController,
				sala -> new Object[]{sala.getId(), sala.getNome(), sala.getEstado(),
						sala.getDepartamento() == null ? "-" : sala.getDepartamento().getNome()},
				"ADMIN".equalsIgnoreCase(perfil) || "SECRETARIO".equalsIgnoreCase(perfil)
						? this::criarSala : null);
	}

	private void adicionarCursos() {
		adicionarTabela("Cursos", new String[]{"ID", "Nome", "Departamento"}, cursoController,
				curso -> new Object[]{curso.getId(), curso.getNome(),
						curso.getDepartamento() == null ? "-" : curso.getDepartamento().getNome()},
				"ADMIN".equalsIgnoreCase(perfil) ? this::criarCurso : null);
	}

	private void adicionarDepartamentos() {
		adicionarTabela("Departamentos", new String[]{"ID", "Nome"}, departamentoController,
				departamento -> new Object[]{departamento.getId(), departamento.getNome()}, null);
	}

	private void adicionarDisciplinas() {
		adicionarTabela("Disciplinas", new String[]{"ID", "Disciplina", "Docente", "Curso"},
				disciplinaController, disciplina -> new Object[]{disciplina.getId(), disciplina.getNome(),
						disciplina.getDocente() == null ? "-" : disciplina.getDocente().getNomeCompleto(),
						disciplina.getCurso() == null ? "-" : disciplina.getCurso().getNome()}, null);
	}

	private void adicionarDocentes() {
		adicionarTabela("Docentes", new String[]{"ID", "Nome", "E-mail", "Nivel academico"}, docenteController,
				docente -> new Object[]{docente.getId(), docente.getNomeCompleto(), docente.getEmail(),
						docente.getNivelAcademico()}, null);
	}

	private void adicionarEstudantes() {
		adicionarTabela("Estudantes", new String[]{"ID", "Nome", "E-mail", "Curso", "Ano"}, estudanteController,
				estudante -> new Object[]{estudante.getId(), estudante.getNomeCompleto(), estudante.getEmail(),
						estudante.getCursoNome(), estudante.getAno()}, null);
	}

	private void adicionarSecretarios() {
		adicionarTabela("Secretarios", new String[]{"ID", "Nome", "E-mail", "Cargo"}, secretarioController,
				secretario -> new Object[]{secretario.getId(), secretario.getNomeCompleto(),
						secretario.getEmail(), secretario.getCargo()}, null);
	}

	private void adicionarTurmas() {
		adicionarTabela("Turmas", new String[]{"ID", "Chave", "Curso", "Ano"}, turmaController,
				turma -> new Object[]{turma.getId(), turma.getChave(), turma.getCursoNome(), turma.getAno()}, null);
	}

	private void adicionarHorarios() {
		adicionarTabela("Horarios", new String[]{"ID", "Dia", "Inicio", "Fim", "Curso", "Disciplina"},
				horarioController, horario -> new Object[]{horario.getId(), horario.getDiaSemana(),
						horario.getHoraInicio(), horario.getHoraFim(), horario.getNomeCurso(),
						horario.getDisciplina() == null ? "-" : horario.getDisciplina().getNome()}, null);
	}

	private void adicionarReservas() {
		adicionarTabela("Reservas", new String[]{"ID", "Sala", "Docente", "Disciplina", "Data", "Inicio", "Fim",
				"Estado"}, reservaController, this::mapearReserva, null);
	}

	private void adicionarInscricoes() {
		adicionarTabela("Inscricoes", new String[]{"ID", "Estudante", "Disciplina"}, inscricaoController,
				inscricao -> new Object[]{inscricao.getId(),
						inscricao.getEstudante() == null ? "-" : inscricao.getEstudante().getNomeCompleto(),
						inscricao.getDisciplina() == null ? "-" : inscricao.getDisciplina().getNome()}, null);
	}

	private <T> void adicionarTabela(String titulo, String[] colunas, Controller<T> controller,
									 Function<T, Object[]> mapear, Runnable criar) {
		tabs.addTab(titulo, criarTabelaPanel(titulo, colunas, controller, mapear, criar));
	}

	private JPanel criarInicio() {
		JPanel panel = new JPanel(new BorderLayout(0, 16));
		panel.setBackground(PAPER);
		panel.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
		JLabel greeting = new JLabel("Bem-vindo, " + email);
		greeting.setFont(new Font("Segoe UI", Font.BOLD, 24));
		greeting.setForeground(INK);
		JLabel description = new JLabel("Aceda pelo menu lateral as ferramentas disponiveis para "
				+ nomePerfil(perfil).toLowerCase() + ".");
		description.setForeground(new Color(92, 105, 97));
		JPanel intro = new JPanel(new BorderLayout(0, 10));
		intro.setOpaque(false);
		intro.add(greeting, BorderLayout.NORTH);
		intro.add(description, BorderLayout.CENTER);
		panel.add(intro, BorderLayout.NORTH);
		return panel;
	}

	private Object[] mapearReserva(Reserva reserva) {
		return new Object[]{reserva.getId(),
				reserva.getSalaId() == null ? "-" : reserva.getSalaId().getNome(),
				reserva.getDocenteId() == null ? "-" : reserva.getDocenteId().getNomeCompleto(),
				reserva.getDisciplina() == null ? "-" : reserva.getDisciplina().getNome(),
				reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(), reserva.getEstadoReserva()};
	}

	private JPanel criarCabecalho() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(INK);
		header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
		JLabel title = new JLabel("Gestão de salas");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel account = new JLabel(nomePerfil(perfil) + "  |  " + email);
		account.setForeground(new Color(216, 231, 218));
		JPanel accountPanel = new JPanel(new GridBagLayout());
		accountPanel.setOpaque(false);
		accountPanel.add(account);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.insets = new Insets(0, 14, 0, 0);
		JButton logout = new JButton("Terminar sessao");
		logout.addActionListener(event -> {
			new MenuUI().setVisible(true);
			dispose();
		});
		accountPanel.add(logout, constraints);
		header.add(title, BorderLayout.WEST);
		header.add(accountPanel, BorderLayout.EAST);
		return header;
	}

	private <T> JPanel criarTabelaPanel(String titulo, String[] colunas, Controller<T> controller,
										Function<T, Object[]> mapear, Runnable criar) {
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(PAPER);
		panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
		JLabel heading = new JLabel(titulo);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
		heading.setForeground(INK);
		panel.add(heading, BorderLayout.NORTH);
		DefaultTableModel model = new DefaultTableModel(colunas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable table = new JTable(model);
		table.setRowHeight(30);
		table.setFillsViewportHeight(true);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		JButton refresh = new JButton("Atualizar");
		JButton add = criar == null ? null : new JButton("Adicionar");
		JPanel actions = new JPanel(new GridBagLayout());
		actions.setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 8);
		constraints.anchor = GridBagConstraints.WEST;
		actions.add(refresh, constraints);
		if (add != null) {
			constraints.gridx = 1;
			actions.add(add, constraints);
		}
		panel.add(actions, BorderLayout.SOUTH);
		Runnable reload = () -> carregar(controller, mapear, model, refresh);
		refresh.addActionListener(event -> reload.run());
		if (add != null) {
			add.addActionListener(event -> criar.run());
		}
		panel.putClientProperty("reload", reload);
		return panel;
	}

	private <T> void carregar(Controller<T> controller, Function<T, Object[]> mapear,
							  DefaultTableModel model, JButton refresh) {
		refresh.setEnabled(false);
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		try {
			List<T> items = controller.listar();
			model.setRowCount(0);
			for (T item : items) {
				model.addRow(mapear.apply(item));
			}
		} catch (RuntimeException e) {
			mostrarErro("Nao foi possivel carregar os dados: " + mensagem(e));
		} finally {
			refresh.setEnabled(true);
			setCursor(java.awt.Cursor.getDefaultCursor());
		}
	}

	private void criarSala() {
		JTextField nome = new JTextField();
		if (JOptionPane.showConfirmDialog(this, new Object[]{"Nome da sala", nome}, "Nova sala",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION || nome.getText().isBlank()) {
			return;
		}
		executarOperacao("A guardar sala...", () -> salaController.salvar(
				new Sala(salaController.gerarProximoId(), nome.getText().trim())), "Sala registada.");
	}

	private void criarCurso() {
		JTextField nome = new JTextField();
		if (JOptionPane.showConfirmDialog(this, new Object[]{"Nome do curso", nome}, "Novo curso",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION || nome.getText().isBlank()) {
			return;
		}
		executarOperacao("A guardar curso...", () -> cursoController.salvar(
				new Curso(cursoController.gerarProximoId(), nome.getText().trim())), "Curso registado.");
	}

	private void executarOperacao(String titulo, Runnable operacao, String sucesso) {
		setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		try {
			operacao.run();
			JOptionPane.showMessageDialog(AdminUI.this, sucesso, titulo, JOptionPane.INFORMATION_MESSAGE);
			atualizarSelecionado();
		} catch (RuntimeException e) {
			mostrarErro("Operacao falhou: " + mensagem(e));
		} finally {
			setCursor(java.awt.Cursor.getDefaultCursor());
		}
	}

	private void atualizarSelecionado() {
		if (tabs.getSelectedComponent() instanceof JPanel panel) {
			Object reload = panel.getClientProperty("reload");
			if (reload instanceof Runnable action) {
				action.run();
			}
		}
	}

	private void mostrarErro(String texto) {
		JOptionPane.showMessageDialog(this, texto, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	private String mensagem(Exception exception) {
		Throwable causa = exception.getCause() != null ? exception.getCause() : exception;
		return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
	}

	private static String nomePerfil(String codigo) {
		switch (codigo.toUpperCase()) {
			case "ADMIN":
				return "Administrador";
			case "SECRETARIO":
				return "Secretario";
			case "DOCENTE":
				return "Docente";
			case "ESTUDANTE":
				return "Estudante";
			default:
				return codigo;
		}
	}
}