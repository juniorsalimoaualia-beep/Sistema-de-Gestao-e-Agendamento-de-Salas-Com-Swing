package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.DiaSemana;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.service.EmailGenarator;
import java.time.LocalTime;
import java.util.List;

public class AdminUI {
    private Validacao vd = new Validacao();
    private EmailGenarator emailGen=new EmailGenarator();
    private SecretarioController secretarioController = new SecretarioController();
    private DocenteController docenteController = new DocenteController();
    private EstudanteController estudanteController = new EstudanteController();
    private DepartamentoController departamentoController = new DepartamentoController();
    private CursoController cursoController = new CursoController();
    private DisciplinaController disciplinaController = new DisciplinaController();
    private HorarioController horarioController = new HorarioController();
    private SalaController salaController= new SalaController();
    
    public void menu() {
        while (true) {
            System.out.println("\n--- Menu Admin ---");
            int opcao = vd.validarInt("1. Registrar Secretario\n2. Registrar Docente\n3. Registrar Estudante\n4. Registrar Departamento\n5. Registrar Curso\n6. Registrar Disciplina\n7. Registrar Sala\n8. Editar Docente\n9. Deletar Docente\n10. Editar Curso\n11. Editar Disciplina\n12. Registrar Horario\n0. Voltar");
            switch (opcao) {
                case 1:
                    cadastrarSecretario();
                    break;
                case 2:
                    cadastrarDocente();
                    break;
                case 3:
                    cadastrarEstudante();
                    break;
                case 4:
                    cadastrarDepartamento();
                    break;
                case 5:
                    cadastrarCurso();
                    break;
                case 6:
                    cadastrarDisciplina();
                    break;
                case 7:
                    cadastrarSala();
                    break;
                case 8:
                    editarDocente();
                    break;
                    
                case 9:
                    deletarDocente();
                    break;
                case 10:
                    editarCurso();
                    break;
                case 11:
                    editarDisciplina();
                    break;
                case 12:
                    cadastrarHorario();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 12.");
            }
        }
    }

    private void cadastrarSecretario() {
        System.out.println("\n--- Registrar Secretario ---");
        int id = secretarioController.gerarProximoId();
        String nome = vd.validarString("Nome:");
        String apelido = vd.validarString("Apelido:");
        String cargo = vd.validarString("Cargo:");
        int numCel = vd.validarCell("Numero de celular:");
        String email = emailGen.gerarEmail(nome, apelido);
        String senha = vd.validarSenha("Senha:");
        System.out.println("O seu email gerado e:"+email);
        Secretario secretario = new Secretario(id, nome, apelido, cargo, numCel, email, senha);
        secretarioController.salvar(secretario);
        System.out.println("Secretario registrado com sucesso. ID: " + id);
    }

    private void cadastrarDocente() {
        System.out.println("\n--- Registrar Docente ---");
        int id = docenteController.gerarProximoId();
        String nome = vd.validarString("Nome:");
        String apelido = vd.validarString("Apelido:");
        String nivel = vd.validarString("Nivel academico:");
        int numCel = vd.validarCell("Numero de celular:");
        String email = emailGen.gerarEmail(nome, apelido);
        String senha = vd.validarSenha("Senha:");
        Docente docente = new Docente(id, nome, apelido, nivel, numCel, email, senha);
        docenteController.salvar(docente);
        System.out.println("Docente registrado com sucesso. ID: " + id);
    }

    private void cadastrarEstudante() {
        System.out.println("\n--- Registrar Estudante ---");
        int id = estudanteController.gerarProximoId();
        String nome = vd.validarString("Nome:");
        String apelido = vd.validarString("Apelido:");
        String curso = vd.validarString("Curso:");
        int numCel = vd.validarCell("Numero de celular:");
        String email= emailGen.gerarEmail(nome,apelido);
        String senha = vd.validarSenha("Senha:");
        System.out.println("O seu email gerado e:"+email);
        Estudante estudante = new Estudante(id, nome, apelido, curso, numCel, email, senha);
        estudanteController.salvar(estudante);
        System.out.println("Estudante registrado com sucesso. ID: " + id);
    }

    private void cadastrarDepartamento() {
        System.out.println("\n--- Registrar Departamento ---");
        int id = departamentoController.gerarProximoId();
        String nome = vd.validarString("Nome do departamento:");
        Departamento departamento = new Departamento(id, nome);
        departamentoController.salvar(departamento);
        System.out.println("Departamento registrado com sucesso. ID: " + id);
    }

    private void cadastrarCurso() {
        System.out.println("\n--- Registrar Curso ---");
        int id = cursoController.gerarProximoId();
        String nome = vd.validarString("Nome do curso:");
        Curso curso = new Curso(id, nome);
        cursoController.salvar(curso);
        System.out.println("Curso registrado com sucesso. ID: " + id);
    }

    private void cadastrarDisciplina() {
        System.out.println("\n--- Registrar Disciplina ---");
        int id = disciplinaController.gerarProximoId();
        String nome = vd.validarString("Nome da disciplina:");
        
        // Listar docentes
        java.util.List<Docente> docentes = docenteController.listar();
        if (docentes.isEmpty()) {
            System.out.println("Nenhum docente disponivel. Por favor, registre um docente primeiro.");
            return;
        }
        
        System.out.println("\n--- Selecione um Docente ---");
        for (Docente d : docentes) {
            System.out.println(d.getId() + ". " + d.getNomeCompleto() + " (" + d.getNivelAcademico() + ")");
        }
        
        int docenteId = vd.validarInt("Digite o ID do docente:");
        Docente docenteSelecionado = docenteController.buscarPorId(docenteId);
        
        if (docenteSelecionado == null) {
            System.out.println("Docente nao encontrado.");
            return;
        }

        Curso cursoSelecionado = selecionarCurso();
        if (cursoSelecionado == null) {
            return;
        }
        
        Disciplina disciplina = new Disciplina(id, nome, docenteSelecionado, cursoSelecionado.getNome());
        disciplinaController.salvar(disciplina);
        System.out.println("Disciplina registrada com sucesso. ID: " + id + " | Docente: " + docenteSelecionado.getNomeCompleto() + " | Curso: " + cursoSelecionado.getNome());
    }

    private void cadastrarHorario() {
        System.out.println("\n--- Registrar Horario ---");
        Curso cursoSelecionado = selecionarCurso();
        if (cursoSelecionado == null) {
            return;
        }

        List<Disciplina> disciplinas = disciplinaController.listarPorCurso(cursoSelecionado.getNome());
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina encontrada para este curso.");
            return;
        }

        System.out.println("\n--- Selecione uma Disciplina ---");
        for (Disciplina disciplina : disciplinas) {
            System.out.println(disciplina.getId() + ". " + disciplina.getNome());
        }

        int disciplinaId = vd.validarInt("Digite o ID da disciplina:");
        Disciplina disciplinaSelecionada = disciplinaController.buscarPorId(disciplinaId);
        if (disciplinaSelecionada == null || !cursoSelecionado.getNome().equalsIgnoreCase(disciplinaSelecionada.getCurso())) {
            System.out.println("Disciplina nao encontrada para o curso selecionado.");
            return;
        }

        int id = horarioController.gerarProximoId();
        DiaSemana diaSemana = vd.validarReserva("Dia da semana:", DiaSemana.class);
        LocalTime horaInicio = vd.validarTime("Hora inicio (HH:mm):");
        LocalTime horaFim = vd.validarTime("Hora fim (HH:mm):");
        if (!horaFim.isAfter(horaInicio)) {
            System.out.println("Hora fim deve ser depois da hora inicio.");
            return;
        }

        Horario horario = new Horario(id, cursoSelecionado, disciplinaSelecionada, diaSemana, horaInicio, horaFim);
        horarioController.salvar(horario);
        System.out.println("Horario registrado com sucesso. ID: " + id + " | Curso: " + cursoSelecionado.getNome() + " | Disciplina: " + disciplinaSelecionada.getNome());
    }

    private Curso selecionarCurso() {
        List<Curso> cursos = cursoController.listar();
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso disponivel. Por favor, registre um curso primeiro.");
            return null;
        }

        System.out.println("\n--- Selecione um Curso ---");
        for (Curso curso : cursos) {
            System.out.println(curso.getId() + ". " + curso.getNome());
        }

        int cursoId = vd.validarInt("Digite o ID do curso:");
        Curso cursoSelecionado = cursoController.buscarCursoPorId(cursoId);
        if (cursoSelecionado == null) {
            System.out.println("Curso nao encontrado.");
        }
        return cursoSelecionado;
    }

    private void editarDisciplina(){
        System.out.println("\n--- Editar Disciplina ---");
        int id= vd.validarInt("Digite o ID da disciplina a editar:");
        Disciplina disc = disciplinaController.buscarPorId(id);
        
        if(disc==null){
            System.out.println("Discilina nao encontrada");
            return;
        }
        System.out.println("Docente atual da disciplina: "+disc.getDocente().getNomeCompleto());
        int idDocente=disc.getDocente().getId();
        String nome=vd.validarString("Digite o nome do novo docente:");
        String apelido=vd.validarString("Digite o apelido:");
        disc.getDocente().setNome(nome);
        disc.getDocente().setApelido(apelido); 
        System.out.println("Docente atual: "+disc.getDocente().getNomeCompleto());

    }

    private void editarDocente() {
        System.out.println("\n--- Editar Docente ---");
        int id = vd.validarInt("Digite o ID do docente a editar:");
        Docente docente = docenteController.buscarPorId(id);
        
        if (docente == null) {
            System.out.println("Docente nao encontrado.");
            return;
        }
        
        System.out.println("Docente atual: " + docente.getNomeCompleto());
        String nome = vd.validarString("Novo nome (ou pressione Enter para manter):");
        if (nome.isEmpty()) nome = docente.getNome();
        
        String apelido = vd.validarString("Novo apelido (ou pressione Enter para manter):");
        if (apelido.isEmpty()) apelido = docente.getApelido();
        
        String nivel = vd.validarString("Novo nivel academico (ou pressione Enter para manter):");
        if (nivel.isEmpty()) nivel = docente.getNivelAcademico();
        
        int numCel = vd.validarCell("Novo numero de celular (0 para manter):");
        if (numCel == 0) numCel = docente.getNumCel();
        
        String email = docente.getEmail();
        docenteController.editarDocente(id, nome, apelido, nivel, numCel, email);
        System.out.println("Docente atualizado com sucesso.");
    }

    private void deletarDocente() {
        System.out.println("\n--- Deletar Docente ---");
        int id = vd.validarCell("Digite o ID do docente a deletar:");
        Docente docente = docenteController.buscarPorId(id);
        
        if (docente == null) {
            System.out.println("Docente nao encontrado.");
            return;
        }
        
        System.out.println("Tem certeza que deseja deletar " + docente.getNomeCompleto() + "? (s/n)");
        String confirmacao = vd.validarString("Confirmacao:");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            if (docenteController.deletarDocente(id)) {
                System.out.println("Docente deletado com sucesso.");
            } else {
                System.out.println("Erro ao deletar docente.");
            }
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    private void editarCurso() {
        System.out.println("\n--- Editar Curso ---");
        int id = vd.validarCell("Digite o ID do curso a editar:");
        Curso curso = cursoController.buscarCursoPorId(id);
        
        if (curso == null) {
            System.out.println("Curso nao encontrado.");
            return;
        }
        
        System.out.println("Curso atual: " + curso.getNome());
        String novoNome = vd.validarString("Novo nome do curso:");
        
        if (novoNome.isEmpty()) {
            System.out.println("Nome nao pode estar vazio.");
            return;
        }
        
        cursoController.editarCurso(id, novoNome);
        System.out.println("Curso atualizado com sucesso.");
    }

    public void cadastrarSala(){
        String nome=vd.validarString("Digite o nome da Sala:");
        int id=salaController.gerarProximoId();
        Sala sala= new Sala(id, nome);
        salaController.salvar(sala);
    }

}
