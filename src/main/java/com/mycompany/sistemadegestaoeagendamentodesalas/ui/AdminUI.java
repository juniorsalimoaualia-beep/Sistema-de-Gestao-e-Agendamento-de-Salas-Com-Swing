package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.service.EmailGenarator;

public class AdminUI {
    private Validacao vd = new Validacao();
    private EmailGenarator emailGen=new EmailGenarator();
    private SecretarioController secretarioController = new SecretarioController();
    private DocenteController docenteController = new DocenteController();
    private EstudanteController estudanteController = new EstudanteController();
    private DepartamentoController departamentoController = new DepartamentoController();
    private CursoController cursoController = new CursoController();
    private DisciplinaController disciplinaController = new DisciplinaController();
    
    public void menu() {
        while (true) {
            System.out.println("\n--- Menu Admin ---");
            int opcao = vd.validarNumero("1. Registrar Secretario\n2. Registrar Docente\n3. Registrar Estudante\n4. Registrar Departamento\n5. Registrar Curso\n6. Registrar Disciplina\n0. Voltar");
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
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 6.");
            }
        }
    }

    private void cadastrarSecretario() {
        System.out.println("\n--- Registrar Secretario ---");
        int id = secretarioController.gerarProximoId();
        String nome = vd.validarString("Nome:");
        String apelido = vd.validarString("Apelido:");
        String cargo = vd.validarString("Cargo:");
        int numCel = vd.validarNumero("Numero de celular:");
        String email = emailGen.gerarEmail(nome, apelido);
        String senha = vd.validarString("Senha:");
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
        int numCel = vd.validarNumero("Numero de celular:");
        String email = emailGen.gerarEmail(nome, apelido);
        String senha = vd.validarString("Senha:");
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
        int numCel = vd.validarNumero("Numero de celular:");
        String email= emailGen.gerarEmail(nome,apelido);
        String senha = vd.validarString("Senha:");
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
        String docente = vd.validarString("Nome do docente responsavel:");
        Disciplina disciplina = new Disciplina(id, nome, docente);
        disciplinaController.salvar(disciplina);
        System.out.println("Disciplina registrada com sucesso. ID: " + id);
    }

}
