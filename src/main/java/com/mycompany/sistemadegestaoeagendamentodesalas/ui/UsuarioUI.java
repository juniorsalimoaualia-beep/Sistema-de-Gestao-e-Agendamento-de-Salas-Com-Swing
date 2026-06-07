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
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.SecretarioDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller.CursoController;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;

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
            int opcao = vd.validarInt("1. Perfil\n2. Consultar Sala\n3. Ver Disciplinas\n4. Inscrever-se em Disciplina\n5. Alterar senha\n0. Sair");
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
            int opcao = vd.validarInt("1. Perfil\n2. Ver Estudantes\n3. Reservar Sala\n4. Estado da Reserva\n5. Alterar senha\n0. Sair");
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
                    } else {
                        Reserva reserva = new Reserva(idReserva, sala.getId(), idDocenteReserva, disciplinaEscolhida, turma, data, horaInicio, horaFim);
                        reservaController.salvar(reserva);
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
                    String senhaAtual=vd.validarString("Digite a senha atual:");
                    String senhaNova=vd.validarString("Digite nova senha:");
                    docenteController.alterarSenha(usuarioLogadoId, senhaAtual, senhaNova);
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
            int opcao = vd.validarInt("1. Perfil\n2. Ver Reservas\n3. Confirmar Reservas\n4. Cancelar Reserva\n5. Alterar senha\n0. Sair");
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
                    String senhaAtual=vd.validarString("Digite a senha atual:");
                    String senhaNova=vd.validarString("Digite nova senha:");
                    secretarioController.alterarSenha(usuarioLogadoId, senhaAtual, senhaNova);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida. Digite 0 a 4.");
            }
        }
    }

    public void menuAdmin(){
        adminUI.menu();
    }

}
