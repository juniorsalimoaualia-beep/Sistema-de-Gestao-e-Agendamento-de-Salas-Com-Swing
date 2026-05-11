package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.AutenticacaoDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DocenteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DisciplinaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.DocenteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.EstudanteController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.ReservaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SalaController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.SecretarioController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.service.InscricaoService;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Inscricao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.SecretarioDAO;

public class UsuarioUI {
    private Validacao vd = new Validacao();
    private AutenticacaoDAO autenticacao = new AutenticacaoDAO();
    private SalaController salaController = new SalaController();
    private DisciplinaController disciplinaController = new DisciplinaController();
    private EstudanteController estudanteController = new EstudanteController();
    private DocenteController docenteController = new DocenteController();
    private ReservaController reservaController = new ReservaController();
    private SecretarioController secretarioController = new SecretarioController();
    private EstudanteDAO esDAO=new EstudanteDAO();
    private DocenteDAO dcDAO=new DocenteDAO();
    private SecretarioDAO scDAO=new SecretarioDAO();
    private AdminUI adminUI = new AdminUI();
    private InscricaoService inscricaoService = new InscricaoService();
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
            int opcao = vd.validarNumero("1. Tentar novamente\n0. Voltar");
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
        
        String email = vd.validarString("Digite o seu email:");
        String senha = vd.validarString("Digite a sua senha:");
        usuarioLogadoId=retornarID(perfil, email);

        return autenticacao.autenticarLogin(perfil, email, senha);
    }

    public void menuEstudante(){
        
        while(true){
            System.out.println("\n--- Menu Estudante ---");
            int opcao = vd.validarNumero("1. Perfil\n2. Consultar Sala\n3. Ver Disciplinas\n4. Inscrever-se em Disciplina\n0. Sair");
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
                    String nomeSala = vd.validarString("Digite o nome da sala:");
                    Sala sala = salaController.buscarPorNome(nomeSala);
                    if(sala == null){
                        System.out.println("Sala nao encontrada.");
                    } else {
                        System.out.println("Sala: " + sala.getNome() + " | ID: " + sala.getId());
                    }
                    break;
                case 3:
                    List<Inscricao> inscricoes = inscricaoService.listarInscricoesPorEstudante(usuarioLogadoId);
                    if(inscricoes.isEmpty()){
                        System.out.println("Voce nao esta inscrito em nenhuma disciplina.");
                    } else {
                        System.out.println("Disciplinas em que voce esta inscrito:");
                        for(Inscricao inscricao : inscricoes){
                            System.out.println(inscricao.getDisciplina().toString());
                        }
                    }
                    break;
                case 4:
                    int disciplinaId = vd.validarNumero("Digite o ID da disciplina para se inscrever:");
                    if(inscricaoService.inscreverEstudante(usuarioLogadoId, disciplinaId)){
                        System.out.println("Inscricao realizada com sucesso.");
                    } else {
                        System.out.println("Falha na inscricao. Verifique se a disciplina existe ou se ja esta inscrito.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 4.");
            }
        }
    }

    public void menuDocente(){
        
        while(true){
            System.out.println("\n--- Menu Docente ---");
            int opcao = vd.validarNumero("1. Perfil\n2. Ver Estudantes\n3. Reservar Sala\n0. Sair");
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
                                    System.out.println("  - " + inscricao.getEstudante().toString());
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    int idReserva = reservaController.gerarProximoId();
                    String salaNome = vd.validarString("Nome da sala:");
                    Sala sala = salaController.buscarPorNome(salaNome);
                    int idDocenteReserva = usuarioLogadoId; // Usar o ID logado
                    Docente docenteReserva = docenteController.buscarPorId(idDocenteReserva);
                    String disciplina = vd.validarString("Disciplina:");
                    String turma = vd.validarString("Turma:");
                    LocalDate data = vd.validarDate("Data (dd/MM/yyyy):");
                    LocalTime horaInicio = vd.validarTime("Hora inicio (HH:mm):");
                    LocalTime horaFim = vd.validarTime("Hora fim (HH:mm):");
                    if(sala == null){
                        System.out.println("Sala nao encontrada.");
                    } else if(docenteReserva == null){
                        System.out.println("Docente nao encontrado.");
                    } else {
                        Reserva reserva = new Reserva(idReserva, sala, docenteReserva, disciplina, turma, data, horaInicio, horaFim);
                        reservaController.salvar(reserva);
                        System.out.println("Reserva cadastrada com sucesso. ID: " + idReserva);
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 3.");
            }
        }
    }

    public void menuSecretario(){
        while(true){
            System.out.println("\n--- Menu Secretario ---");
            int opcao = vd.validarNumero("1. Perfil\n2. Ver Reservas\n3. Cancelar Reserva\n0. Sair");
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
                    int idReserva = vd.validarNumero("Digite o ID da reserva para cancelar:");
                    if(reservaController.cancelar(idReserva)){
                        System.out.println("Reserva cancelada com sucesso.");
                    } else {
                        System.out.println("Nao foi possivel cancelar a reserva.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 3.");
            }
        }
    }

    public void menuAdmin(){
        adminUI.menu();
    }

}
