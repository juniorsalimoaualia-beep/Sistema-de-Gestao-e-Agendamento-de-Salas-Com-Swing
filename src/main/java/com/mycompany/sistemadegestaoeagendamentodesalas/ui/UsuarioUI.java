package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DocenteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.HorarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.service.InscricaoService;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Inscricao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.SecretarioDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.EstadoReserva;

public class UsuarioUI {
    private Validacao vd = new Validacao();
    private AutenticacaoDAO autenticacao = new AutenticacaoDAO();
    private SalaController salaController = new SalaController();
    private DisciplinaController disciplinaController = new DisciplinaController();
    private EstudanteController estudanteController = new EstudanteController();
    private HorarioController horarioController = new HorarioController();
    private DocenteController docenteController = new DocenteController();
    private ReservaController reservaController = new ReservaController();
    private SecretarioController secretarioController = new SecretarioController();
    private EstudanteDAO esDAO=new EstudanteDAO();
    private DocenteDAO dcDAO=new DocenteDAO();
    private SecretarioDAO scDAO=new SecretarioDAO();
    private AdminUI adminUI = new AdminUI();
    private InscricaoService inscricaoService = new InscricaoService();
    private CursoController cursoController = new CursoController();
    private int usuarioLogadoId;
    
    public void login(int escolha){
        String perfil;
        switch(escolha){
            case 1:
                perfil = "Admin";
                break;
            case 2:
                perfil = "Docente";
                break;
            case 3:
                perfil = "Estudante";
                break;
            case 4:
                perfil = "Secretario";
                break;
            default:
                System.out.println("Opcao invalida. Volte ao menu principal e selecione uma opcao valida.");
                return;
        }

        while(true){
            boolean autenticado = attemptLogin(perfil);
            if(autenticado){
                switch(escolha){
                    case 1:
                        menuAdmin();
                        return;
                    case 2:
                        menuDocente();
                        return;
                    case 3:
                        menuEstudante();
                        return;
                    case 4:
                        menuSecretario();
                        return;
                }
            }

            System.out.println("Login falhou.");
            int opcao = vd.validarInt("1. Tentar novamente\n0. Voltar");
            if(opcao != 1){
                System.out.println("Retornando ao menu principal.");
                return;
            }
        }
    }

    
    public int retornarID(String perfil, String email){
        List<Estudante> listaEs=esDAO.listaEstudante();
        List<Docente> listaDc=dcDAO.listaDocente();
        List<Secretario> listaSec= scDAO.listaSecretario();

        if(perfil.equals("Estudante")){
            for(Estudante es:listaEs){
                if(es.getEmail().equals(email)){
                    return es.getId();   
                }
              
            }
            
            
        }
        
        else if(perfil.equals("Docente")){
            for(Docente dc:listaDc){
                if(dc.getEmail().equals(email)){
                    return dc.getId();
                }
                  
            }
        }

        else if(perfil.equals("Secretario")){
            for(Secretario sc:listaSec){
                if(sc.getEmail().equals(email)){
                    return sc.getId();
                }
            }
        }
        return 0;
    }

    private boolean attemptLogin(String perfil){
        System.out.println("\n--- Login de " + perfil + " ---");
        
        String email = vd.validarEmail("Digite o seu email:");
        String senha = vd.validarSenha("Digite a sua senha:");
        usuarioLogadoId=retornarID(perfil, email);

        return autenticacao.autenticarLogin(perfil, email, senha);
    }

    public void menuEstudante(){
        
        while(true){
            System.out.println("\n--- Menu Estudante ---");
            int opcao = vd.validarInt("1. Perfil\n2. Consultar Sala\n3. Ver Disciplinas\n4. Inscrever-se em Disciplina\n5. Alterar senha\n6. Ver Horario\n0. Sair");
            switch(opcao){
                case 1:
                    Estudante estudante = estudanteController.buscarPorId(usuarioLogadoId);
                    if(estudante == null){
                        System.out.println("Estudante nao encontrado.");
                    } else {
                        System.out.println("Perfil:\nId: " + estudante.getId()+"\nNome: "+estudante.getNomeCompleto()+"\nEmail: "
                                            +estudante.getEmail()+"\nCurso: "+estudante.getCurso()+"\nContacto: "+estudante.getNumCel());
                    }
                    break;
                case 2:
                    consultarSalaPorDisciplinaInscrita();
                    break;
                case 3:
                    List<Inscricao> inscricoes = inscricaoService.listarInscricoesPorEstudante(usuarioLogadoId);
                    if(inscricoes.isEmpty()){
                        System.out.println("Voce nao esta inscrito em nenhuma disciplina.");
                    } else {
                        System.out.println("Disciplinas em que voce esta inscrito:");
                        for(Inscricao inscricao : inscricoes){
                            System.out.println(inscricao.getDisciplina().getNome()+" - "+inscricao.getDisciplina().getDocente());
                        }
                    }
                    break;
                case 4:
                    List<Disciplina> disciplinasDisponiveis = disciplinaController.listar();
                    if(disciplinasDisponiveis.isEmpty()){
                        System.out.println("Nenhuma disciplina disponivel para inscricao.");
                        break;
                    }
                    for(Disciplina disciplina : disciplinasDisponiveis){
                        System.out.println(disciplina.getId() + " - " + disciplina.getNome());
                    }
                    int disciplinaId = vd.validarInt("Digite o ID da disciplina para se inscrever:");
                    if(inscricaoService.inscreverEstudante(usuarioLogadoId, disciplinaId)){
                        System.out.println("Inscricao realizada com sucesso.");
                    } else {
                        System.out.println("Falha na inscricao. Verifique se a disciplina existe ou se ja esta inscrito.");
                    }
                    break;
                case 5:
                    String senhaAtual=vd.validarString("Digite a senha atual:");
                    String senhaNova=vd.validarString("Digite nova senha:");
                    estudanteController.alterarSenha(usuarioLogadoId, senhaAtual, senhaNova);
                    break;
                case 6:
                    mostrarHorarioEstudante();
                    break;

                    
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 6.");
            }
        }
    }

    private void mostrarHorarioEstudante() {
        List<Inscricao> inscricoes = inscricaoService.listarInscricoesPorEstudante(usuarioLogadoId);
        if(inscricoes.isEmpty()){
            System.out.println("Voce nao esta inscrito em nenhuma disciplina.");
            return;
        }

        boolean encontrouHorario = false;
        System.out.println("\n--- Seu Horario ---");
        for(Inscricao inscricao : inscricoes){
            Disciplina disciplina = inscricao.getDisciplina();
            List<Horario> horarios = horarioController.listarPorDisciplina(disciplina.getId());

            if(horarios.isEmpty()){
                System.out.println("Disciplina: " + disciplina.getNome() + " | Sem horario cadastrado.");
                continue;
            }

            for(Horario horario : horarios){
                System.out.println("Disciplina: " + disciplina.getNome()
                        + " | Curso: " + disciplina.getCurso()
                        + " | Dia: " + horario.getDiaSemana()
                        + " | Horario: " + horario.getHoraInicio() + "-" + horario.getHoraFim());
                encontrouHorario = true;
            }
        }

        if(!encontrouHorario){
            System.out.println("Nenhuma das suas disciplinas tem horario cadastrado.");
        }
    }

    private void consultarSalaPorDisciplinaInscrita() {
        List<Inscricao> inscricoes = inscricaoService.listarInscricoesPorEstudante(usuarioLogadoId);
        if(inscricoes.isEmpty()){
            System.out.println("Voce nao esta inscrito em nenhuma disciplina.");
            return;
        }

        System.out.println("Disciplinas em que voce esta inscrito:");
        for(int i = 0; i < inscricoes.size(); i++){
            Disciplina disciplina = inscricoes.get(i).getDisciplina();
            System.out.println((i + 1) + ". " + disciplina.getNome());
        }

        int escolha = vd.validarInt("Selecione o numero da disciplina:");
        if(escolha < 1 || escolha > inscricoes.size()){
            System.out.println("Opcao invalida.");
            return;
        }

        Disciplina disciplinaSelecionada = inscricoes.get(escolha - 1).getDisciplina();
        List<Reserva> reservas = reservaController.listar();
        boolean encontrou = false;

        for(Reserva reserva : reservas){
            if(reserva.getDisciplina() != null
                    && reserva.getDisciplina().getId() == disciplinaSelecionada.getId()
                    && reserva.getEstadoReserva() != EstadoReserva.CANCELADA
                    && reserva.getEstadoReserva() != EstadoReserva.RECUSADA){
                Sala sala = salaController.buscarPorId(reserva.getSalaId());
                String nomeSala = sala != null ? sala.getNome() : "Sala ID " + reserva.getSalaId();
                System.out.println("Disciplina: " + disciplinaSelecionada.getNome()
                        + " | Sala: " + nomeSala
                        + " | Data: " + reserva.getData()
                        + " | Horario: " + reserva.getHoraInicio() + "-" + reserva.getHoraFim()
                        + " | Estado: " + reserva.getEstadoReserva());
                encontrou = true;
            }
        }

        if(!encontrou){
            System.out.println("Ainda nao existe sala/reserva marcada para esta disciplina.");
        }
    }

    public void menuDocente(){
        
        while(true){
            System.out.println("\n--- Menu Docente ---");
            int opcao = vd.validarInt("1. Perfil\n2. Ver Estudantes\n3. Reservar Sala\n4. Estado da Reserva\n5. Cancelar Reserva\n6. Alterar senha\n0. Sair");
            switch(opcao){
                case 1:
                    Docente docente = docenteController.buscarPorId(usuarioLogadoId);
                    if(docente == null){
                        System.out.println("Docente nao encontrado.");
                    } else {
                        System.out.println("Perfil: "+"\nId: "+docente.getId()+"\nNome: "+docente.getNomeCompleto()+"\nNivel Academico: "+docente.getNivelAcademico()+
                                        "\nEmail: "+docente.getEmail()+"\nContacto: "+docente.getNumCel());
                    }
                    break;
                case 2:
                    // Listar estudantes inscritos nas disciplinas do docente
                    List<Disciplina> disciplinasDocente = disciplinaController.listarPorDocente(usuarioLogadoId);
                    if(disciplinasDocente.isEmpty()){
                        System.out.println("Voce nao tem disciplinas cadastradas.");
                    } else {
                        System.out.println("Estudantes inscritos nas suas disciplinas:");
                        for(Disciplina disciplina : disciplinasDocente){
                            List<Inscricao> inscricoes = inscricaoService.listarInscricoesPorDisciplina(disciplina.getId());
                            if(!inscricoes.isEmpty()){
                                System.out.println("Disciplina: " + disciplina.getNome());
                                for(Inscricao inscricao : inscricoes){
                                    System.out.println(inscricao.getEstudante().getId() + " | " + inscricao.getEstudante().getNomeCompleto()+" | "+inscricao.getEstudante().getCurso()+" | "+inscricao.getEstudante().getEmail());
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    // Listar disciplinas do docente e selecionar uma
                    List<Disciplina> disciplinasDoDocente = disciplinaController.listarPorDocente(usuarioLogadoId);
                    if(disciplinasDoDocente.isEmpty()){
                        System.out.println("Voce nao tem disciplinas cadastradas para fazer reserva.");
                        break;
                    }
                    
                    Disciplina disciplinaEscolhida = null;
                    if (disciplinasDoDocente.size() == 1) {
                        disciplinaEscolhida = disciplinasDoDocente.get(0);
                        System.out.println("Disciplina selecionada automaticamente: " + disciplinaEscolhida.getNome());
                    } else {
                        System.out.println("Disciplinas disponiveis para reserva:");
                        for(int i = 0; i < disciplinasDoDocente.size(); i++){
                            System.out.println((i+1) + ". " + disciplinasDoDocente.get(i).getNome());
                        }
                        int escolhaDisciplina = vd.validarInt("Selecione o numero da disciplina:") - 1;
                        if(escolhaDisciplina < 0 || escolhaDisciplina >= disciplinasDoDocente.size()){
                            System.out.println("Opcao invalida.");
                            break;
                        }
                        disciplinaEscolhida = disciplinasDoDocente.get(escolhaDisciplina);
                    }

                    int idReserva = reservaController.gerarProximoId();
                    String salaNome = vd.validarString("Nome da sala:");
                    Sala sala = salaController.buscarPorNome(salaNome);
                    int idDocenteReserva = usuarioLogadoId;
                    Docente docenteReserva = docenteController.buscarPorId(idDocenteReserva);

                    // Escolha do curso para a turma
                    String cursoPadrao = disciplinaEscolhida.getCurso();
                    List<Curso> cursos = cursoController.listar();
                    System.out.println("Cursos disponiveis:");
                    for (int i = 0; i < cursos.size(); i++) {
                        System.out.println((i+1) + ". " + cursos.get(i).getNome());
                    }
                    System.out.println("0. Usar curso da disciplina: " + (cursoPadrao == null || cursoPadrao.isEmpty() ? "(nenhum)" : cursoPadrao));
                    int escolhaCurso = vd.validarInt("Selecione o numero do curso (0 para usar o padrao):");
                    String cursoEscolhido = cursoPadrao;
                    if (escolhaCurso > 0 && escolhaCurso <= cursos.size()) {
                        cursoEscolhido = cursos.get(escolhaCurso - 1).getNome();
                    } else if (escolhaCurso == 0 && (cursoPadrao == null || cursoPadrao.isEmpty())) {
                        System.out.println("Nenhum curso padrao definido. Escolha um curso valido.");
                        break;
                    }

                    int anoTurma = vd.validarInt("Digite o ano da turma (ex: 1):");
                    Turma turma = new Turma(anoTurma, cursoEscolhido);
                    LocalDate data = vd.validarDate("Data (dd/MM/yyyy):");
                    LocalTime horaInicio = vd.validarTime("Hora inicio (HH:mm):");
                    LocalTime horaFim = vd.validarTime("Hora fim (HH:mm):");
                    
                    if(sala == null){
                        System.out.println("Sala nao encontrada.");
                    } else if(docenteReserva == null){
                        System.out.println("Docente nao encontrado.");
                    } else if(!horaFim.isAfter(horaInicio)){
                        System.out.println("Hora fim deve ser depois da hora inicio.");
                    } else if(!reservaController.verificarDisponibilidade(salaNome, data, horaInicio, horaFim)){
                        System.out.println("Sala indisponivel para este periodo.");
                    } else {
                        Reserva reserva = new Reserva(idReserva, sala.getId(), idDocenteReserva, disciplinaEscolhida, turma, data, horaInicio, horaFim);
                        reservaController.salvar(reserva);
                        reservaController.vincularSalaAReserva(sala.getId(), idReserva);
                        System.out.println("Reserva cadastrada com sucesso. ID: " + idReserva);
                    }
                    break;
                case 4:
                    List<Reserva> rs= reservaController.listar();
                    for(Reserva lista:rs){
                        if(lista.getDocenteId()==usuarioLogadoId){
                            System.out.println("Estado da reserva: "+lista.getEstadoReserva()+"\nTurma: "+lista.getTurma()+"\nData: "+lista.getData()+"\nHorario: "+lista.getHoraInicio()+"-"+lista.getHoraFim());
                        }
                    }
                    
                    break;
                case 5:
                    int reservaCancelar = vd.validarInt("Digite o ID da sua reserva para cancelar:");
                    if(reservaController.cancelarDoDocente(reservaCancelar, usuarioLogadoId)){
                        System.out.println("Reserva cancelada com sucesso.");
                    } else {
                        System.out.println("Nao foi possivel cancelar. Verifique o ID, o docente da reserva ou o estado da reserva.");
                    }
                    break;
                case 6:
                    String senhaAtual=vd.validarString("Digite a senha atual:");
                    String senhaNova=vd.validarString("Digite nova senha:");
                    docenteController.alterarSenha(usuarioLogadoId, senhaAtual, senhaNova);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 6.");
            }
        }
    }

    public void menuSecretario(){
        while(true){
            System.out.println("\n--- Menu Secretario ---");
            int opcao = vd.validarInt("1. Perfil\n2. Ver Reservas\n3. Confirmar Reservas\n4. Cancelar Reserva\n5. Estado da Reserva\n6. Alterar senha\n7. Relatorios\n0. Sair");
            switch(opcao){
                case 1:
                    Secretario secretario = secretarioController.buscarPorId(usuarioLogadoId);
                    if(secretario == null){
                        System.out.println("Secretario nao encontrado.");
                    } else {
                        System.out.println("Perfil: \nId:" +secretario.getId()+"\nNome: "+secretario.getNomeCompleto()+"\nEmail:"+secretario.getEmail()+
                                            "\nContacto: "+secretario.getNumCel());
                    }
                    break;
                case 2:
                    List<Reserva> reservas = reservaController.listar();
                    if(reservas.isEmpty()){
                        System.out.println("Nenhuma reserva encontrada.");
                    } else {
                        System.out.println("Reservas cadastradas:");
                        for(Reserva reserva : reservas){
                            System.out.println(reserva.toString());
                        }
                    }
                    break;
                case 3:
                    int reserva= vd.validarInt("Digite o ID da reserva: ");
                    if(reservaController.confirmar(reserva)){
                        System.out.println("Reserva confirmada com sucesso!");
                    }
                    else{
                        System.out.println("Nao foi possivel confirmar a reserva. Verifique se o ID esta correto ou se a reserva ja foi confirmada");
                    }
                    break;
                case 4:
                    int idReserva = vd.validarInt("Digite o ID da reserva para cancelar:");
                    if(reservaController.cancelar(idReserva)){
                        System.out.println("Reserva cancelada com sucesso.");
                    } else {
                        System.out.println("Nao foi possivel cancelar a reserva.");
                    }
                    break;
                case 5:
                    int idEstado = vd.validarInt("Digite o ID da reserva para ver o estado:");
                    System.out.println(reservaController.estadoReserva(idEstado));
                    break;
                case 6:
                    String senhaAtual=vd.validarString("Digite a senha atual:");
                    String senhaNova=vd.validarString("Digite nova senha:");
                    secretarioController.alterarSenha(usuarioLogadoId, senhaAtual, senhaNova);
                    break;
                case 7:
                    menuRelatoriosSecretario();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 7.");
            }
        }
    }

    private void menuRelatoriosSecretario() {
        while(true){
            System.out.println("\n--- Relatorios de Reservas ---");
            int opcao = vd.validarInt("1. Resumo por estado\n2. Reservas por docente\n3. Reservas por sala\n4. Reservas por data\n5. Todas detalhadas\n0. Voltar");
            switch(opcao){
                case 1:
                    relatorioPorEstado();
                    break;
                case 2:
                    relatorioPorDocente();
                    break;
                case 3:
                    relatorioPorSala();
                    break;
                case 4:
                    relatorioPorData();
                    break;
                case 5:
                    imprimirReservasDetalhadas(reservaController.listar());
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 5.");
            }
        }
    }

    private void relatorioPorEstado() {
        List<Reserva> reservas = reservaController.listar();
        if(reservas.isEmpty()){
            System.out.println("Nenhuma reserva encontrada.");
            return;
        }

        for(EstadoReserva estado : EstadoReserva.values()){
            int total = 0;
            for(Reserva reserva : reservas){
                if(reserva.getEstadoReserva() == estado){
                    total++;
                }
            }
            System.out.println(estado + ": " + total);
        }
        System.out.println("Total: " + reservas.size());
    }

    private void relatorioPorDocente() {
        int docenteId = vd.validarInt("Digite o ID do docente:");
        List<Reserva> reservas = reservaController.listar();
        List<Reserva> resultado = new java.util.ArrayList<>();

        for(Reserva reserva : reservas){
            if(reserva.getDocenteId() == docenteId){
                resultado.add(reserva);
            }
        }

        imprimirReservasDetalhadas(resultado);
    }

    private void relatorioPorSala() {
        int salaId = vd.validarInt("Digite o ID da sala:");
        List<Reserva> reservas = reservaController.listar();
        List<Reserva> resultado = new java.util.ArrayList<>();

        for(Reserva reserva : reservas){
            if(reserva.getSalaId() == salaId){
                resultado.add(reserva);
            }
        }

        imprimirReservasDetalhadas(resultado);
    }

    private void relatorioPorData() {
        LocalDate data = vd.validarDate("Data do relatorio");
        List<Reserva> reservas = reservaController.listar();
        List<Reserva> resultado = new java.util.ArrayList<>();

        for(Reserva reserva : reservas){
            if(data.equals(reserva.getData())){
                resultado.add(reserva);
            }
        }

        imprimirReservasDetalhadas(resultado);
    }

    private void imprimirReservasDetalhadas(List<Reserva> reservas) {
        if(reservas.isEmpty()){
            System.out.println("Nenhuma reserva encontrada.");
            return;
        }

        for(Reserva reserva : reservas){
            Sala sala = salaController.buscarPorId(reserva.getSalaId());
            Docente docente = docenteController.buscarPorId(reserva.getDocenteId());
            String nomeSala = sala != null ? sala.getNome() : "Sala ID " + reserva.getSalaId();
            String nomeDocente = docente != null ? docente.getNomeCompleto() : "Docente ID " + reserva.getDocenteId();
            String nomeDisciplina = reserva.getDisciplina() != null ? reserva.getDisciplina().getNome() : "";

            System.out.println("Reserva ID: " + reserva.getId()
                    + " | Estado: " + reserva.getEstadoReserva()
                    + " | Sala: " + nomeSala
                    + " | Docente: " + nomeDocente
                    + " | Disciplina: " + nomeDisciplina
                    + " | Turma: " + reserva.getTurma()
                    + " | Data: " + reserva.getData()
                    + " | Horario: " + reserva.getHoraInicio() + "-" + reserva.getHoraFim());
        }
        System.out.println("Total encontrado: " + reservas.size());
    }

    public void menuAdmin(){
        adminUI.menu();
    }

}
