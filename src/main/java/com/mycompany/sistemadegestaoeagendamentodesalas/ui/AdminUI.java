// Dados: classe AdminUI

package com.mycompany.sistemadegestaoeagendamentodesalas.ui;

import com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DepartamentoController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;
import com.mycompany.sistemadegestaoeagendamentodesalas.service.EmailGenarator;
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
            int opcao = vd.validarInt("1. Registrar Secretario\n2. Registrar Docente\n3. Registrar Estudante\n4. Registrar Departamento\n5. Registrar Curso\n6. Registrar Disciplina\n7. Registrar Sala\n8. Deletar Docente\n9. Editar Disciplina\n10. Registrar Horario\n0. Voltar");
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
                    deletarDocente();
                    break;
                case 9:
                    editarDisciplina();
                    break;
                case 10:
                    cadastrarHorario();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 10.");
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
        Curso cursoSelecionado = selecionarCurso();
        if (cursoSelecionado == null) {
            return;
        }
        int numCel = vd.validarCell("Numero de celular:");
        String email= emailGen.gerarEmail(nome,apelido);
        String senha = vd.validarSenha("Senha:");
        System.out.println("O seu email gerado e:"+email);
        Estudante estudante = new Estudante(id, nome, apelido, cursoSelecionado, numCel, email, senha);
        if (estudanteController.cadastrarEstudante(estudante)) {
            System.out.println("Estudante registrado com sucesso. ID: " + id);
        } else {
            System.out.println("Nao foi possivel registrar o estudante. Verifique se ja existe um cadastro com os mesmos dados.");
        }
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
        if (departamentoController.listar().isEmpty()) {
            System.out.println("Nenhum departamento disponivel. Por favor, registre um departamento primeiro.");
            return;
        }
        String nome = vd.validarString("Nome do curso:");
        Departamento departamentoSelecionado = selecionarDepartamento();
        if (departamentoSelecionado == null) {
            return;
        }
        Curso curso = new Curso(id, nome, departamentoSelecionado);
        cursoController.salvar(curso);
        System.out.println("Curso registrado com sucesso. ID: " + id + " | Departamento: " + departamentoSelecionado.getNome());
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
        
        Disciplina disciplina = new Disciplina(id, nome, docenteSelecionado, cursoSelecionado);
        disciplinaController.salvar(disciplina);
        System.out.println("Disciplina registrada com sucesso. ID: " + id + " | Docente: " + docenteSelecionado.getNomeCompleto() + " | Curso: " + cursoSelecionado.getNome());
    }

    private void cadastrarHorario() {
        System.out.println("\n--- Registrar Horario ---");
        Curso cursoSelecionado = selecionarCurso();
        if (cursoSelecionado == null) {
            return;
        }

        List<Disciplina> disciplinas = disciplinaController.listarPorCurso(cursoSelecionado);
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
        if (disciplinaSelecionada == null || !cursoSelecionado.getNome().equalsIgnoreCase(disciplinaSelecionada.getCursoNome())) {
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

    private Departamento selecionarDepartamento() {
        List<Departamento> departamentos = departamentoController.listar();
        if (departamentos.isEmpty()) {
            System.out.println("Nenhum departamento disponivel. Por favor, registre um departamento primeiro.");
            return null;
        }

        System.out.println("\n--- Selecione um Departamento ---");
        for (Departamento departamento : departamentos) {
            System.out.println(departamento.getId() + ". " + departamento.getNome());
        }

        int departamentoId = vd.validarInt("Digite o ID do departamento:");
        Departamento departamentoSelecionado = departamentoController.buscarPorId(departamentoId);
        if (departamentoSelecionado == null) {
            System.out.println("Departamento nao encontrado.");
        }
        return departamentoSelecionado;
    }

    private void editarDisciplina(){
        System.out.println("\n--- Editar Disciplina ---");
        List<Disciplina> disciplinas = disciplinaController.listar();
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada.");
            return;
        }
        System.out.println("Disciplinas disponiveis:");
        for (Disciplina disciplina : disciplinas) {
            System.out.println(disciplina.getId() + ". " + disciplina.getNome());
        }
        int id= vd.validarInt("Digite o ID da disciplina a editar:");
        Disciplina disc = disciplinaController.buscarPorId(id);
        
        if(disc==null){
            System.out.println("Disciplina nao encontrada");
            return;
        }
        System.out.println("Docente atual da disciplina: "+disc.getDocente().getNomeCompleto());
       
        String nome=vd.validarString("Digite o nome do novo docente:");
        String apelido=vd.validarString("Digite o apelido:");
        disc.getDocente().setNome(nome);
        disc.getDocente().setApelido(apelido); 
        System.out.println("Docente atual: "+disc.getDocente().getNomeCompleto());

    }

    private void deletarDocente() {
        System.out.println("\n--- Deletar Docente ---");
        List<Docente> docentes = docenteController.listar();
        if (docentes.isEmpty()) {
            System.out.println("Nenhum docente cadastrado.");
            return;
        }
        System.out.println("Docentes disponiveis:");
        for (Docente docente : docentes) {
            System.out.println(docente.getId() + ". " + docente.getNomeCompleto());
        }
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

    public void cadastrarSala(){
        if (departamentoController.listar().isEmpty()) {
            System.out.println("Nenhum departamento disponivel. Por favor, registre um departamento primeiro.");
            return;
        }
        String nome = vd.validarSala("Digite o nome da Sala:");
        int id = salaController.gerarProximoId();
        Departamento departamentoSelecionado = selecionarDepartamento();
        if (departamentoSelecionado == null) {
            return;
        }
        Sala sala = new Sala(id, nome, departamentoSelecionado);
        salaController.salvar(sala);
        System.out.println("Sala registrada com sucesso. ID: " + id + " | Nome: " + nome + " | Departamento: " + departamentoSelecionado.getNome());
    }

}
