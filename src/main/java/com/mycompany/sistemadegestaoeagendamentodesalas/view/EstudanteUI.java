package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.InscricaoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class EstudanteUI extends PerfilDashboardUI {
	private static final String AVISO_DEMO = "Modo demonstracao: dados ficticios; nenhuma ligacao a base de dados.";
	private static final String TODOS_CURSOS = "Todas as disciplinas permitidas";
	private static final int LIMITE_INSCRICOES = 7;
	private static final String CURSO_DEMO = "Engenharia Informatica";
	private static final DisciplinaDemo[] DISCIPLINAS_DEMO = {
			new DisciplinaDemo(1, "Programacao Orientada a Objectos", "Ana Mucavele", CURSO_DEMO),
			new DisciplinaDemo(2, "Bases de Dados", "Ana Mucavele", CURSO_DEMO),
			new DisciplinaDemo(3, "Redes de Computadores", "Paulo Cossa", CURSO_DEMO, true),
			new DisciplinaDemo(4, "Desenvolvimento Web", "Paulo Cossa", CURSO_DEMO),
			new DisciplinaDemo(5, "Matematica Geral", "Celina Mavume", "Geral")
	};

	private final EstudanteController estudanteController = new EstudanteController();
	private final DisciplinaController disciplinaController = new DisciplinaController();
	private final HorarioController horarioController = new HorarioController();
	private final InscricaoController inscricaoController = new InscricaoController();
	private final ReservaController reservaController = new ReservaController();
	private final Estudante estudante;
	private final boolean demonstracao;
	private final List<DisciplinaItem> inscricoes = new ArrayList<>();
	private final JPanel listaInscricoes = new JPanel(new GridLayout(0, 1, 0, 10));
	private JPanel cartoesDisciplinas;

	/** Prepara o painel do estudante e carrega os dados da conta ou de demonstracao. */
	public EstudanteUI(String email, int utilizadorId, Runnable voltarMenu) {
		super("ESTUDANTE", email, utilizadorId, voltarMenu);
		demonstracao = utilizadorId <= 0;
		estudante = demonstracao ? null : estudanteController.buscarPorId(utilizadorId);
		if (!demonstracao && estudante == null) {
			throw new IllegalStateException("A conta autenticada nao corresponde a um estudante.");
		}
		carregarInscricoes();
		adicionarHorarios();
		adicionarDisciplinas();
		adicionarInscricoes();
		adicionarEventos();
		finalizarDashboard("ESTUDANTE", demonstracao ? AVISO_DEMO : null);
	}

	/** Carrega as disciplinas inscritas pelo estudante ou os exemplos demonstrativos. */
	private void carregarInscricoes() {
		inscricoes.clear();
		if (demonstracao) {
			inscricoes.add(itemDemo(DISCIPLINAS_DEMO[0]));
			inscricoes.add(itemDemo(DISCIPLINAS_DEMO[1]));
			return;
		}
		for (Inscricao inscricao : inscricaoController.listarPorEstudante(estudante.getId())) {
			if (inscricao.getDisciplina() != null) {
				inscricoes.add(itemReal(inscricao.getDisciplina()));
			}
		}
	}

	/** Monta a grelha semanal com horarios e salas do curso do estudante. */
	private void adicionarHorarios() {
		String curso = demonstracao ? CURSO_DEMO : estudante.getCursoNome();
		List<Horario> horarios = demonstracao ? new ArrayList<>() : horarioController.listarPorCurso(estudante.getCurso());
		DefaultTableModel model = modeloTabela(new String[]{
				"Horario", "SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO"});
		if (demonstracao) {
			model.addRow(new Object[]{"08:00 - 09:30", "Programacao Orientada a Objectos\nSala 12",
					"", "", "", "", ""});
			model.addRow(new Object[]{"10:00 - 11:30", "", "", "Bases de Dados\nSala 08", "", "", ""});
		} else {
			List<Horario> linhas = new ArrayList<>();
			for (Horario horario : horarios) {
				boolean jaExiste = false;
				for (Horario linha : linhas) {
					if (linha.getHoraInicio().equals(horario.getHoraInicio())
							&& linha.getHoraFim().equals(horario.getHoraFim())) {
						jaExiste = true;
						break;
					}
				}
				if (!jaExiste) linhas.add(horario);
			}
			linhas.sort(Comparator.comparing(Horario::getHoraInicio));
			for (Horario linha : linhas) {
				model.addRow(new Object[]{linha.getHoraInicio() + " - " + linha.getHoraFim(),
						"", "", "", "", "", ""});
			}
			for (Horario horario : horarios) {
				int linha = -1;
				String faixa = horario.getHoraInicio() + " - " + horario.getHoraFim();
				for (int i = 0; i < model.getRowCount(); i++) {
					if (faixa.equals(model.getValueAt(i, 0))) {
						linha = i;
						break;
					}
				}
				int coluna = horario.getDiaSemana().ordinal() + 1;
				String disciplina = horario.getDisciplina() == null
						? "Sem disciplina" : horario.getDisciplina().getNome();
				String sala = horario.getSala() == null ? "Sala por definir" : horario.getSala().getNome();
				String conteudo = disciplina + "\n" + horario.getHoraInicio() + " - "
						+ horario.getHoraFim() + "  |  " + sala;
				String existente = (String) model.getValueAt(linha, coluna);
				model.setValueAt(existente.isEmpty() ? conteudo : existente + "\n\n" + conteudo, linha, coluna);
			}
		}
		JTable table = new JTable(model);
		table.setRowHeight(62);
		table.getColumnModel().getColumn(0).setPreferredWidth(105);
		adicionarPainel("Horarios", criarPainelTabela("Horario de " + curso, table));
	}

	/** Apresenta cartoes apenas das disciplinas em que o estudante esta inscrito. */
	private void adicionarDisciplinas() {
		cartoesDisciplinas = new JPanel(new GridLayout(0, 2, 12, 12));
		cartoesDisciplinas.setOpaque(false);
		atualizarCartoesDisciplinas();
		JPanel painel = new JPanel(new BorderLayout(0, 14));
		painel.setBackground(PaletaCores.FUNDO);
		painel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
		painel.add(criarTitulo("Disciplinas do curso e disciplinas gerais"), BorderLayout.NORTH);
		painel.add(new JScrollPane(cartoesDisciplinas), BorderLayout.CENTER);
		adicionarPainel("Disciplinas", painel);
	}

	/** Actualiza os cartoes de disciplinas com as inscricoes actuais do estudante. */
	private void atualizarCartoesDisciplinas() {
		if (cartoesDisciplinas == null) {
			return;
		}
		cartoesDisciplinas.removeAll();
		for (DisciplinaItem disciplina : inscricoes) {
			cartoesDisciplinas.add(criarCartao(disciplina.nome,
					"Docente: " + disciplina.docente + "  |  Curso: " + disciplina.curso));
		}
		if (cartoesDisciplinas.getComponentCount() == 0) {
			cartoesDisciplinas.add(new JLabel("Ainda nao esta inscrito em nenhuma disciplina."));
		}
		cartoesDisciplinas.revalidate();
		cartoesDisciplinas.repaint();
	}

	/** Monta a lista de inscricoes e o acesso para adicionar disciplinas. */
	private void adicionarInscricoes() {
		listaInscricoes.setOpaque(false);
		atualizarListaInscricoes();
		JPanel painel = new JPanel(new BorderLayout(0, 14));
		painel.setBackground(PaletaCores.FUNDO);
		painel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
		painel.add(criarTitulo("As minhas inscricoes"), BorderLayout.NORTH);
		painel.add(new JScrollPane(listaInscricoes), BorderLayout.CENTER);

		JButton inscrever = new JButton("Inscrever-se em disciplinas");
		inscrever.setBackground(PaletaCores.DESTAQUE);
		inscrever.setForeground(Color.WHITE);
		inscrever.setFocusPainted(false);
		inscrever.addActionListener(event -> inscreverEmDisciplinas());
		painel.add(inscrever, BorderLayout.SOUTH);
		adicionarPainel("Inscricoes", painel);
	}

	/** Atualiza visualmente os cartoes das inscricoes atuais. */
	private void atualizarListaInscricoes() {
		listaInscricoes.removeAll();
		if (inscricoes.isEmpty()) {
			JLabel vazio = new JLabel("Ainda nao existem inscricoes.");
			vazio.setForeground(PaletaCores.SECUNDARIA);
			listaInscricoes.add(vazio);
		}
		for (DisciplinaItem disciplina : inscricoes) {
			listaInscricoes.add(criarCartao(disciplina.nome,
					"Docente: " + disciplina.docente + "  |  Curso: " + disciplina.curso));
		}
		listaInscricoes.revalidate();
		listaInscricoes.repaint();
	}

	/** Permite escolher varias disciplinas validas e regista as inscricoes. */
	private void inscreverEmDisciplinas() {
		List<DisciplinaItem> disponiveis = disciplinasPermitidas();
		Set<Integer> jaInscritas = idsInscritos();
		disponiveis.removeIf(disciplina -> jaInscritas.contains(disciplina.id));
		if (disponiveis.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Nao existem outras disciplinas do seu curso disponiveis.",
					"Sem disciplinas", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JComboBox<String> filtro = new JComboBox<>(cursosDisponiveis(disponiveis));
		JPanel listaCaixas = new JPanel(new GridLayout(0, 1, 0, 8));
		Set<Integer> selecionadas = new HashSet<>();
		atualizarCaixasDisciplinas(listaCaixas, disponiveis, filtro, selecionadas);
		filtro.addActionListener(event ->
				atualizarCaixasDisciplinas(listaCaixas, disponiveis, filtro, selecionadas));

		JPanel seletorCurso = new JPanel(new BorderLayout(8, 0));
		seletorCurso.setOpaque(false);
		seletorCurso.add(new JLabel("Filtrar:"), BorderLayout.WEST);
		seletorCurso.add(filtro, BorderLayout.CENTER);
		JPanel conteudo = new JPanel(new BorderLayout(0, 12));
		conteudo.setPreferredSize(new Dimension(460, 300));
		conteudo.add(seletorCurso, BorderLayout.NORTH);
		conteudo.add(new JScrollPane(listaCaixas), BorderLayout.CENTER);
		if (JOptionPane.showConfirmDialog(this, conteudo, "Nova inscricao",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
			return;
		}
		if (selecionadas.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Selecione pelo menos uma disciplina.",
					"Nenhuma disciplina selecionada", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (inscricoes.size() + selecionadas.size() > LIMITE_INSCRICOES) {
			JOptionPane.showMessageDialog(this,
					"So pode adicionar " + (LIMITE_INSCRICOES - inscricoes.size())
							+ " disciplina(s), respeitando o limite de " + LIMITE_INSCRICOES + ".",
					"Limite de inscricoes", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int adicionadas = 0;
		for (DisciplinaItem disciplina : disponiveis) {
			if (!selecionadas.contains(disciplina.id)) {
				continue;
			}
			if (demonstracao) {
				inscricoes.add(disciplina);
				adicionadas++;
			} else if (inscricaoController.salvar(new Inscricao(0, estudante,
					new Disciplina(disciplina.id, disciplina.nome, null)))) {
				adicionadas++;
			} else {
				break;
			}
		}
		if (!demonstracao) {
			carregarInscricoes();
		}
		atualizarCartoesDisciplinas();
		atualizarListaInscricoes();
		if (adicionadas == selecionadas.size()) {
			JOptionPane.showMessageDialog(this, demonstracao
					? "Inscricoes adicionadas a lista demonstrativa (nao foram enviadas a base de dados)."
					: "Inscricoes guardadas com sucesso.");
		} else {
			JOptionPane.showMessageDialog(this, "Foram guardadas " + adicionadas + " de "
					+ selecionadas.size() + " inscricao(oes). Verifique o limite e os dados.",
					"Inscricao parcial", JOptionPane.WARNING_MESSAGE);
		}
	}

	/** Atualiza as caixas de selecao conforme o filtro escolhido. */
	private void atualizarCaixasDisciplinas(JPanel painel, List<DisciplinaItem> disciplinas,
			JComboBox<String> filtro, Set<Integer> selecionadas) {
		painel.removeAll();
		String selecionado = (String) filtro.getSelectedItem();
		for (DisciplinaItem disciplina : disciplinas) {
			if (TODOS_CURSOS.equals(selecionado) || disciplina.curso.equals(selecionado)) {
				JCheckBox caixa = new JCheckBox(disciplina.nome + "  |  " + disciplina.docente);
				caixa.setOpaque(true);
				caixa.setBackground(Color.WHITE);
				caixa.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(PaletaCores.BORDA),
						BorderFactory.createEmptyBorder(9, 10, 9, 10)));
				caixa.setSelected(selecionadas.contains(disciplina.id));
				caixa.addActionListener(event -> {
					if (caixa.isSelected()) {
						selecionadas.add(disciplina.id);
					} else {
						selecionadas.remove(disciplina.id);
					}
				});
				painel.add(caixa);
			}
		}
		painel.revalidate();
		painel.repaint();
	}

	/** Cria as opcoes de filtro a partir dos cursos das disciplinas disponiveis. */
	private String[] cursosDisponiveis(List<DisciplinaItem> disciplinas) {
		Set<String> cursos = new LinkedHashSet<>();
		cursos.add(TODOS_CURSOS);
		for (DisciplinaItem disciplina : disciplinas) {
			cursos.add(disciplina.curso);
		}
		return cursos.toArray(new String[0]);
	}

	/** Retorna disciplinas do curso do estudante e disciplinas gerais. */
	private List<DisciplinaItem> disciplinasPermitidas() {
		List<DisciplinaItem> disciplinas = new ArrayList<>();
		if (demonstracao) {
			for (DisciplinaDemo disciplina : DISCIPLINAS_DEMO) {
				if (!disciplina.outroCurso) disciplinas.add(itemDemo(disciplina));
			}
			return disciplinas;
		}
		Curso cursoEstudante = estudante.getCurso();
		if (cursoEstudante == null || cursoEstudante.getId() <= 0) {
			return disciplinas;
		}
		for (Disciplina disciplina : disciplinaController.listar()) {
			Curso cursoDisciplina = disciplina.getCurso();
			if (cursoDisciplina == null || cursoDisciplina.getId() <= 0
					|| cursoDisciplina.getId() == cursoEstudante.getId()) {
				disciplinas.add(itemReal(disciplina));
			}
		}
		return disciplinas;
	}

	/** Reune os identificadores das disciplinas ja inscritas. */
	private Set<Integer> idsInscritos() {
		Set<Integer> ids = new HashSet<>();
		for (DisciplinaItem inscricao : inscricoes) ids.add(inscricao.id);
		return ids;
	}

	/** Converte uma disciplina real para os dados apresentados pela interface. */
	private DisciplinaItem itemReal(Disciplina disciplina) {
		String curso = disciplina.getCurso() == null || disciplina.getCurso().getId() <= 0
				? "Geral" : disciplina.getCurso().getNome();
		String docente = disciplina.getDocente() == null
				? "Sem docente" : disciplina.getDocente().getNomeCompleto();
		return new DisciplinaItem(disciplina.getId(), disciplina.getNome(), docente, curso);
	}

	/** Converte uma disciplina de demonstracao para o formato da interface. */
	private DisciplinaItem itemDemo(DisciplinaDemo disciplina) {
		return new DisciplinaItem(disciplina.id, disciplina.nome, disciplina.docente, disciplina.curso);
	}

	/** Apresenta os eventos e as reservas associados ao curso do estudante. */
	private void adicionarEventos() {
		JPanel cartoes = new JPanel(new GridLayout(0, 1, 0, 10));
		cartoes.setOpaque(false);
		if (demonstracao) {
			cartoes.add(criarCartao("AULA | Programacao Orientada a Objectos",
					"26/10/2026  |  08:00 - 09:30  |  Sala 12  |  Ana Mucavele  |  CONFIRMADA"));
			cartoes.add(criarCartao("AULA | Bases de Dados",
					"28/10/2026  |  10:00 - 11:30  |  Sala 08  |  Ana Mucavele  |  CONFIRMADA"));
		} else {
			for (Reserva reserva : reservaController.listar()) {
				if (!reservaDoCurso(reserva)) continue;
				String disciplina = reserva.getDisciplina() == null ? "Evento geral"
						: reserva.getDisciplina().getNome();
				String sala = reserva.getSalaId() == null ? "Sala por definir" : reserva.getSalaId().getNome();
				String docente = reserva.getDocenteId() == null ? "Docente por definir"
						: reserva.getDocenteId().getNomeCompleto();
				cartoes.add(criarCartao(reserva.getTipoReserva() + " | " + disciplina,
						reserva.getData() + "  |  " + reserva.getHoraInicio() + " - "
								+ reserva.getHoraFim() + "  |  " + sala + "  |  " + docente
								+ "  |  " + reserva.getEstadoReserva()));
			}
		}
		if (cartoes.getComponentCount() == 0) {
			cartoes.add(new JLabel("Nao ha eventos ou reservas para o seu curso."));
		}
		JPanel painel = new JPanel(new BorderLayout(0, 14));
		painel.setBackground(PaletaCores.FUNDO);
		painel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
		painel.add(criarTitulo("Eventos do curso"), BorderLayout.NORTH);
		painel.add(new JScrollPane(cartoes), BorderLayout.CENTER);
		adicionarPainel("Eventos", painel);
	}

	/** Verifica se uma reserva pertence ao curso do estudante. */
	private boolean reservaDoCurso(Reserva reserva) {
		if (estudante.getCurso() == null) return false;
		if (reserva.getTurma() != null && reserva.getTurma().getCurso() != null) {
			return reserva.getTurma().getCurso().getId() == estudante.getCurso().getId();
		}
		Disciplina disciplina = reserva.getDisciplina();
		return disciplina != null && (disciplina.getCurso() == null
				|| disciplina.getCurso().getId() <= 0
				|| disciplina.getCurso().getId() == estudante.getCurso().getId());
	}

	/** Cria um cartao visual com titulo e informacao complementar. */
	private JPanel criarCartao(String titulo, String detalhe) {
		JPanel cartao = new JPanel(new BorderLayout(0, 8));
		cartao.setBackground(Color.WHITE);
		cartao.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaCores.BORDA),
				BorderFactory.createEmptyBorder(14, 16, 14, 16)));
		JLabel nome = new JLabel(titulo);
		nome.setFont(new Font("Segoe UI", Font.BOLD, 15));
		nome.setForeground(PaletaCores.TEXTO);
		JLabel informacao = new JLabel(detalhe);
		informacao.setForeground(PaletaCores.SECUNDARIA);
		cartao.add(nome, BorderLayout.NORTH);
		cartao.add(informacao, BorderLayout.CENTER);
		return cartao;
	}

	/** Cria um titulo estilizado para as seccoes do painel. */
	private JLabel criarTitulo(String texto) {
		JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
		titulo.setForeground(PaletaCores.TEXTO);
		return titulo;
	}

	private static final class DisciplinaDemo {
		private final int id;
		private final String nome;
		private final String docente;
		private final String curso;
		private final boolean outroCurso;

		/** Cria uma disciplina de demonstracao sem marcacao de curso externo. */
		private DisciplinaDemo(int id, String nome, String docente, String curso) {
			this(id, nome, docente, curso, false);
		}

		/** Cria uma disciplina demonstrativa e indica se pertence a outro curso. */
		private DisciplinaDemo(int id, String nome, String docente, String curso, boolean outroCurso) {
			this.id = id;
			this.nome = nome;
			this.docente = docente;
			this.curso = curso;
			this.outroCurso = outroCurso;
		}
	}

	private static final class DisciplinaItem {
		private final int id;
		private final String nome;
		private final String docente;
		private final String curso;

		/** Armazena os dados de disciplina usados nos cartoes e selecoes. */
		private DisciplinaItem(int id, String nome, String docente, String curso) {
			this.id = id;
			this.nome = nome;
			this.docente = docente;
			this.curso = curso;
		}
	}
}
