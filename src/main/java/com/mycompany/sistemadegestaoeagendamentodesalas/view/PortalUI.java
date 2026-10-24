package com.mycompany.sistemadegestaoeagendamentodesalas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PortalUI extends JPanel {
    private static final Color BG = new Color(244, 245, 239);
    private static final Color SURFACE = new Color(255, 255, 252);
    private static final Color FOREST = new Color(27, 68, 56);
    private static final Color TEXT = new Color(37, 45, 40);
    private static final Color MUTED = new Color(112, 121, 113);
    private static final Color CORAL = new Color(207, 102, 70);
    private static final Color LINE = new Color(222, 225, 216);

    private String perfil = "";
    private String email = "";
    private JPanel conteudo;
    private JLabel rodape;
    private JButton botaoAtivo;
        private DefaultTableModel inscricoesEstudante = new DefaultTableModel(
            new String[]{"ID", "Disciplina", "Estado"}, 0);

    /** Inicializa o portal demonstrativo e apresenta os perfis disponiveis. */
    public PortalUI() {
        setLayout(new BorderLayout());
        mostrarPerfis();
    }

    /** Mostra os cartoes para escolher um perfil de acesso. */
    private void mostrarPerfis() {
        JPanel pagina = new JPanel(new BorderLayout(0, 28));
        pagina.setBackground(BG);
        pagina.setBorder(BorderFactory.createEmptyBorder(38, 56, 25, 56));

        JPanel topo = new JPanel(new BorderLayout(0, 9));
        topo.setOpaque(false);
        JLabel marca = texto("SGSAS     /     PORTAL ACADEMICO", 12, Font.BOLD, CORAL);
        JPanel titulo = new JPanel(new GridLayout(2, 1, 0, 0));
        titulo.setOpaque(false);
        titulo.add(texto("Gestao de espacos,", 34, Font.BOLD, FOREST));
        titulo.add(texto("sem perder o fio.", 34, Font.BOLD, FOREST));
        JLabel descricao = texto("Escolhe o teu perfil para continuar.", 14, Font.PLAIN, MUTED);
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 5));
        titulos.setOpaque(false);
        titulos.add(titulo);
        titulos.add(descricao);
        topo.add(marca, BorderLayout.NORTH);
        topo.add(titulos, BorderLayout.CENTER);
        pagina.add(topo, BorderLayout.NORTH);

        JPanel perfis = new JPanel(new GridLayout(2, 2, 14, 14));
        perfis.setOpaque(false);
        perfis.add(cartaoPerfil("01", "Administrador", "Gestao e configuracao do sistema", "ADMIN"));
        perfis.add(cartaoPerfil("02", "Secretario", "Salas, reservas e horarios", "SECRETARIO"));
        perfis.add(cartaoPerfil("03", "Docente", "Disciplinas e espacos de aula", "DOCENTE"));
        perfis.add(cartaoPerfil("04", "Estudante", "Horario e inscricoes", "ESTUDANTE"));
        pagina.add(perfis, BorderLayout.CENTER);
        pagina.add(texto("AMBIENTE DE DEMONSTRACAO   /   autenticacao e dados ainda desligados", 10,
                Font.PLAIN, MUTED), BorderLayout.SOUTH);
        trocarPagina(pagina);
    }

    /** Cria um cartao de perfil e liga a selecao ao formulario de acesso. */
    private JPanel cartaoPerfil(String numero, String nome, String detalhe, final String codigo) {
        JPanel cartao = new JPanel(new BorderLayout(0, 10));
        cartao.setBackground(SURFACE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE), BorderFactory.createEmptyBorder(18, 19, 16, 19)));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(texto("PERFIL  " + numero, 10, Font.BOLD, CORAL), BorderLayout.WEST);
        cabecalho.add(texto("↗", 16, Font.BOLD, FOREST), BorderLayout.EAST);
        cartao.add(cabecalho, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);
        info.add(texto(nome, 23, Font.BOLD, FOREST));
        info.add(texto(detalhe, 12, Font.PLAIN, MUTED));
        cartao.add(info, BorderLayout.CENTER);

        JButton escolher = new JButton("Continuar como " + nome.toLowerCase());
        escolher.setHorizontalAlignment(SwingConstants.LEFT);
        escolher.setForeground(FOREST);
        escolher.setBackground(new Color(239, 242, 234));
        escolher.setBorder(BorderFactory.createEmptyBorder(9, 11, 9, 11));
        escolher.setFocusPainted(false);
        cartao.add(escolher, BorderLayout.SOUTH);
        escolher.addActionListener(new ActionListener() {
            /** Guarda o perfil selecionado e abre o formulario de acesso. */
            @Override
            public void actionPerformed(ActionEvent evento) {
                perfil = codigo;
                mostrarLogin();
            }
        });
        return cartao;
    }

    /** Apresenta o formulario de acesso do perfil escolhido. */
    private void mostrarLogin() {
        JPanel pagina = new JPanel(new GridLayout(1, 2));
        pagina.setBackground(BG);
        pagina.add(painelMarcaLogin());
        pagina.add(painelFormularioLogin());
        trocarPagina(pagina);
    }

    /** Cria o painel visual de identificacao da area de acesso. */
    private JPanel painelMarcaLogin() {
        JPanel painel = new JPanel(new BorderLayout(0, 18));
        painel.setBackground(FOREST);
        painel.setBorder(BorderFactory.createEmptyBorder(38, 38, 34, 32));
        painel.add(texto("SGSAS     /     " + nomePerfil(perfil).toUpperCase(), 12, Font.BOLD,
                new Color(226, 176, 126)), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(3, 1, 0, 10));
        centro.setOpaque(false);
        centro.add(texto("O teu espaco.\nA tua rotina.", 33, Font.BOLD, SURFACE));
        centro.add(texto("Acesso dedicado para " + nomePerfil(perfil).toLowerCase() + ".", 14,
                Font.PLAIN, new Color(213, 224, 215)));
        centro.add(texto("MODO PROTOTIPO  /  sem ligacao ao backend", 10, Font.BOLD,
                new Color(226, 176, 126)));
        painel.add(centro, BorderLayout.CENTER);
        painel.add(texto("GESTAO DE SALAS E HORARIOS", 10, Font.PLAIN,
                new Color(213, 224, 215)), BorderLayout.SOUTH);
        return painel;
    }

    /** Constroi e valida o formulario de e-mail e senha. */
    private JPanel painelFormularioLogin() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BG);
        painel.setBorder(BorderFactory.createEmptyBorder(45, 42, 38, 42));

        JPanel formulario = new JPanel(new GridLayout(7, 1, 0, 8));
        formulario.setOpaque(false);
        formulario.add(texto("Iniciar sessao", 28, Font.BOLD, FOREST));
        formulario.add(texto("Perfil selecionado: " + nomePerfil(perfil), 13, Font.PLAIN, MUTED));
        formulario.add(texto("E-mail", 12, Font.BOLD, TEXT));
        JTextField campoEmail = new JTextField();
        campoEmail.setPreferredSize(new Dimension(100, 38));
        formulario.add(campoEmail);
        formulario.add(texto("Senha", 12, Font.BOLD, TEXT));
        JPasswordField campoSenha = new JPasswordField();
        formulario.add(campoSenha);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setOpaque(false);
        JButton entrar = botaoPrimario("Entrar na dashboard");
        JButton voltar = new JButton("Voltar");
        botoes.add(entrar);
        botoes.add(voltar);
        formulario.add(botoes);
        painel.add(formulario, BorderLayout.CENTER);
        painel.add(texto("Prototipo: qualquer e-mail e senha preenchidos permitem continuar.",
                11, Font.PLAIN, MUTED), BorderLayout.SOUTH);

        entrar.addActionListener(new ActionListener() {
            /** Verifica os campos preenchidos e abre a dashboard demonstrativa. */
            @Override
            public void actionPerformed(ActionEvent evento) {
                if (campoEmail.getText().trim().length() == 0 || campoSenha.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(PortalUI.this, "Preenche os dois campos para continuar.",
                            "Faltam dados", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                email = campoEmail.getText().trim();
                mostrarDashboard();
            }
        });
        voltar.addActionListener(new ActionListener() {
            /** Volta a selecao de perfis sem iniciar a sessao. */
            @Override
            public void actionPerformed(ActionEvent evento) {
                mostrarPerfis();
            }
        });
        return painel;
    }

    /** Monta a estrutura da dashboard e abre a seccao inicial. */
    private void mostrarDashboard() {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.add(barraSuperior(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(menuLateral(), BorderLayout.WEST);
        conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(BG);
        conteudo.setBorder(BorderFactory.createEmptyBorder(25, 28, 20, 28));
        centro.add(conteudo, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);

        rodape = texto("MODO DE DEMONSTRACAO  /  sem JDBC, sem gravacao", 10, Font.PLAIN, MUTED);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 17, 8, 17));
        pagina.add(rodape, BorderLayout.SOUTH);
        trocarPagina(pagina);
        mostrarSecao("Visao geral");
    }

    /** Cria a barra superior com a conta e a acao de terminar sessao. */
    private JPanel barraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FOREST);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 16));
        barra.add(texto("SGSAS   /   " + nomePerfil(perfil).toUpperCase(), 12, Font.BOLD, SURFACE),
                BorderLayout.WEST);
        JPanel utilizador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        utilizador.setOpaque(false);
        utilizador.add(texto(email, 12, Font.PLAIN, SURFACE));
        JButton sair = new JButton("Terminar sessao");
        utilizador.add(sair);
        barra.add(utilizador, BorderLayout.EAST);
        sair.addActionListener(new ActionListener() {
            /** Termina a sessao demonstrativa e regressa a selecao de perfis. */
            @Override
            public void actionPerformed(ActionEvent evento) {
                perfil = "";
                email = "";
                mostrarPerfis();
            }
        });
        return barra;
    }

    /** Cria a navegacao lateral com as seccoes permitidas para o perfil. */
    private JPanel menuLateral() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 5));
        menu.setBackground(new Color(235, 237, 228));
        menu.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        menu.setPreferredSize(new Dimension(185, 120));
        String[] secoes = secoesDoPerfil();
        for (int i = 0; i < secoes.length; i++) {
            final String secao = secoes[i];
            JButton botao = new JButton(secao);
            botao.setHorizontalAlignment(SwingConstants.LEFT);
            botao.setBackground(new Color(235, 237, 228));
            botao.setForeground(TEXT);
            botao.setBorder(BorderFactory.createEmptyBorder(9, 11, 9, 8));
            botao.setFocusPainted(false);
            menu.add(botao);
            botao.addActionListener(new ActionListener() {
            /** Atualiza a seccao ativa e apresenta o conteudo escolhido. */
            @Override
                public void actionPerformed(ActionEvent evento) {
                    if (botaoAtivo != null) {
                        botaoAtivo.setBackground(new Color(235, 237, 228));
                        botaoAtivo.setForeground(TEXT);
                    }
                    botaoAtivo = botao;
                    botaoAtivo.setBackground(FOREST);
                    botaoAtivo.setForeground(SURFACE);
                    mostrarSecao(secao);
                }
            });
        }
        return menu;
    }

    /** Atualiza o conteudo central para a seccao selecionada. */
    private void mostrarSecao(String secao) {
        conteudo.removeAll();
        conteudo.add(cabecalhoSecao(secao), BorderLayout.NORTH);
        conteudo.add(tabelaSecao(secao), BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }

    /** Cria o cabecalho da seccao com saudacao e indicadores. */
    private JPanel cabecalhoSecao(String secao) {
        JPanel painel = new JPanel(new BorderLayout(0, 9));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        painel.add(texto("Bom dia, " + primeiroNome(), 12, Font.BOLD, CORAL), BorderLayout.NORTH);
        painel.add(texto(secao, 28, Font.BOLD, FOREST), BorderLayout.CENTER);

        JPanel metricas = new JPanel(new GridLayout(1, 3, 10, 0));
        metricas.setOpaque(false);
        metricas.add(cartaoMetrica("12", "Salas", "no sistema"));
        metricas.add(cartaoMetrica("08", "Reservas", "esta semana"));
        metricas.add(cartaoMetrica("03", "Pendentes", "para rever"));
        painel.add(metricas, BorderLayout.SOUTH);
        return painel;
    }

    /** Cria um cartao compacto para apresentar uma metrica. */
    private JPanel cartaoMetrica(String numero, String titulo, String detalhe) {
        JPanel cartao = new JPanel(new GridLayout(3, 1, 0, 2));
        cartao.setBackground(SURFACE);
        cartao.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(9, 11, 8, 8)));
        cartao.add(texto(numero, 22, Font.BOLD, CORAL));
        cartao.add(texto(titulo, 12, Font.BOLD, FOREST));
        cartao.add(texto(detalhe, 10, Font.PLAIN, MUTED));
        return cartao;
    }

    /** Monta a tabela e as acoes demonstrativas da seccao atual. */
    private JPanel tabelaSecao(String secao) {
        JPanel painel = new JPanel(new BorderLayout(0, 9));
        painel.setOpaque(false);
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(texto("Atividade recente", 15, Font.BOLD, TEXT), BorderLayout.WEST);
        topo.add(texto("DADOS DE EXEMPLO", 10, Font.BOLD, MUTED), BorderLayout.EAST);
        painel.add(topo, BorderLayout.NORTH);

        String[] colunas = colunasDaSecao(secao);
        DefaultTableModel modelo = modeloDaSecao(secao, colunas);
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(31);
        tabela.setFillsViewportHeight(true);
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(LINE));
        painel.add(scroll, BorderLayout.CENTER);

        JButton acao = botaoDaSecao(secao, tabela, modelo);
        if (acao != null) {
            JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
            acoes.setOpaque(false);
            acoes.add(acao);
            painel.add(acoes, BorderLayout.SOUTH);
        }
        return painel;
    }

    /** Define os titulos das colunas conforme o perfil e a seccao. */
    private String[] colunasDaSecao(String secao) {
        if (perfil.equals("ESTUDANTE") && secao.equals("Disciplinas")) {
            return new String[]{"ID", "Disciplina", "Docente", "Estado"};
        }
        if (perfil.equals("ESTUDANTE") && secao.equals("Inscricoes")) {
            return new String[]{"ID", "Disciplina", "Estado"};
        }
        if (secao.equals("Utilizadores")) {
            return new String[]{"ID", "Nome", "Perfil", "E-mail"};
        }
        return new String[]{"ID", "Item", "Detalhe", "Estado"};
    }

    /** Cria e preenche o modelo de demonstracao da seccao. */
    private DefaultTableModel modeloDaSecao(String secao, String[] colunas) {
        if (perfil.equals("ESTUDANTE") && secao.equals("Inscricoes")) {
            return inscricoesEstudante;
        }
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        if (perfil.equals("ESTUDANTE") && secao.equals("Disciplinas")) {
            modelo.addRow(new String[]{"01", "Bases de dados", "Ana Lima", "Disponivel"});
            modelo.addRow(new String[]{"02", "Programacao I", "Mario Costa", "Disponivel"});
            modelo.addRow(new String[]{"03", "Redes", "Lina Paulo", "Disponivel"});
        } else if (secao.equals("Utilizadores")) {
            modelo.addRow(new String[]{"01", "Ana Lima", "DOCENTE", "ana@exemplo.test"});
            modelo.addRow(new String[]{"02", "Luis Mendes", "ESTUDANTE", "luis@exemplo.test"});
        } else {
            modelo.addRow(new String[]{"01", nomeItem(secao), detalheItem(secao), "Ativo"});
            modelo.addRow(new String[]{"02", "Registo de exemplo", "Apenas para visualizacao", "Pendente"});
        }
        return modelo;
    }

    /** Cria a acao principal aplicavel a seccao e ao perfil atuais. */
    private JButton botaoDaSecao(String secao, JTable tabela, DefaultTableModel modelo) {
        if (perfil.equals("ESTUDANTE")) {
            if (secao.equals("Disciplinas")) {
                JButton inscrever = botaoPrimario("Inscrever-se na disciplina selecionada");
                inscrever.addActionListener(new ActionListener() {
                    /** Valida a selecao e adiciona a inscricao demonstrativa. */
                    @Override
                    public void actionPerformed(ActionEvent evento) {
                        int linha = tabela.getSelectedRow();
                        if (linha < 0) {
                            JOptionPane.showMessageDialog(PortalUI.this,
                                    "Seleciona uma disciplina antes de te inscreveres.");
                            return;
                        }
                        String disciplina = String.valueOf(modelo.getValueAt(linha, 1));
                        if (temInscricao(disciplina)) {
                            JOptionPane.showMessageDialog(PortalUI.this,
                                    "Ja estas inscrito nesta disciplina.");
                            return;
                        }
                        inscricoesEstudante.addRow(new String[]{
                            String.valueOf(inscricoesEstudante.getRowCount() + 1), disciplina, "Pendente"});
                        modelo.setValueAt("Selecionada", linha, 3);
                        rodape.setText("Inscricao de demonstracao adicionada apenas nesta sessao.");
                    }
                });
                return inscrever;
            }
            return null;
        }

        if (perfil.equals("ADMIN")) {
            String texto = secao.equals("Utilizadores") ? "Registar perfil" : "Adicionar registo";
            JButton adicionar = botaoPrimario(texto);
            adicionar.addActionListener(new ActionListener() {
                /** Encaminha a acao para o registo adequado a seccao selecionada. */
                @Override
                public void actionPerformed(ActionEvent evento) {
                    if (secao.equals("Utilizadores")) {
                        registarPerfil(modelo);
                    } else {
                        adicionarRegisto(modelo, secao);
                    }
                }
            });
            return adicionar;
        }
        return null;
    }

    /** Verifica se o estudante ja esta inscrito na disciplina indicada. */
    private boolean temInscricao(String disciplina) {
        for (int i = 0; i < inscricoesEstudante.getRowCount(); i++) {
            if (disciplina.equals(inscricoesEstudante.getValueAt(i, 1))) {
                return true;
            }
        }
        return false;
    }

    /** Recolhe os dados e adiciona um utilizador a tabela demonstrativa. */
    private void registarPerfil(DefaultTableModel modelo) {
        String[] perfis = {"ADMIN", "SECRETARIO", "DOCENTE", "ESTUDANTE"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Escolhe o perfil:",
                "Registar utilizador", JOptionPane.PLAIN_MESSAGE, null, perfis, "ESTUDANTE");
        if (tipo == null) {
            return;
        }
        String entradaNome = JOptionPane.showInputDialog(this, "Nome do " + tipo.toLowerCase() + ":");
        if (entradaNome == null) {
            return;
        }
        String nome;
        String endereco;
        try {
            nome = Validacao.validarNomePessoa(entradaNome, "Nome");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nome invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String entradaEmail = JOptionPane.showInputDialog(this, "E-mail:");
        if (entradaEmail == null) {
            return;
        }
        try {
            endereco = Validacao.validarEnderecoEmail(entradaEmail);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "E-mail invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelo.addRow(new String[]{String.valueOf(modelo.getRowCount() + 1),
            nome, tipo, endereco});
        rodape.setText("Utilizador registado apenas nesta sessao. O backend nao foi chamado.");
    }

    /** Adiciona a tabela um registo demonstrativo para a seccao indicada. */
    private void adicionarRegisto(DefaultTableModel modelo, String secao) {
        String nome = JOptionPane.showInputDialog(this, "Nome para " + secao.toLowerCase() + ":");
        if (nome == null) {
            return;
        }
        try {
            nome = Validacao.validarTextoObrigatorio(nome, "nome", 150);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nome invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelo.addRow(new String[]{String.valueOf(modelo.getRowCount() + 1),
            nome, "Criado nesta sessao", "Teste"});
        rodape.setText("Registo de demonstracao. O backend nao foi chamado.");
    }

    /** Devolve as seccoes visiveis para o perfil atual. */
    private String[] secoesDoPerfil() {
        if (perfil.equals("ADMIN")) {
            return new String[]{"Visao geral", "Salas", "Cursos", "Utilizadores", "Reservas"};
        }
        if (perfil.equals("SECRETARIO")) {
            return new String[]{"Visao geral", "Salas", "Reservas", "Horarios"};
        }
        if (perfil.equals("DOCENTE")) {
            return new String[]{"Visao geral", "Meu horario", "Disciplinas", "Pedir sala"};
        }
        return new String[]{"Visao geral", "Meu horario", "Inscricoes", "Disciplinas"};
    }

    /** Escolhe um nome de exemplo para os itens de cada seccao. */
    private String nomeItem(String secao) {
        if (secao.equals("Salas")) {
            return "Sala 101";
        }
        if (secao.equals("Reservas") || secao.equals("Pedir sala")) {
            return "Reserva de sala";
        }
        if (secao.equals("Meu horario") || secao.equals("Horarios")) {
            return "Aula de exemplo";
        }
        if (secao.equals("Cursos")) {
            return "Informatica";
        }
        if (secao.equals("Utilizadores")) {
            return "Docente de exemplo";
        }
        if (secao.equals("Inscricoes")) {
            return "Bases de dados";
        }
        return "Atividade do dia";
    }

    /** Escolhe um detalhe demonstrativo para a seccao indicada. */
    private String detalheItem(String secao) {
        if (secao.equals("Reservas") || secao.equals("Pedir sala")) {
            return "Quarta-feira, 10:00";
        }
        if (secao.equals("Meu horario") || secao.equals("Horarios")) {
            return "Segunda-feira, 08:00";
        }
        return "Informacao de demonstracao";
    }

    /** Obtem um identificador de saudacao a partir do e-mail atual. */
    private String primeiroNome() {
        if (email.indexOf('@') > 0) {
            return email.substring(0, email.indexOf('@'));
        }
        return nomePerfil(perfil);
    }

    /** Converte o codigo do perfil para o respetivo nome visivel. */
    private String nomePerfil(String codigo) {
        if (codigo.equals("ADMIN")) {
            return "Administrador";
        }
        if (codigo.equals("SECRETARIO")) {
            return "Secretario";
        }
        if (codigo.equals("DOCENTE")) {
            return "Docente";
        }
        return "Estudante";
    }

    /** Cria um rotulo com estilo tipografico e cor definidos. */
    private JLabel texto(String valor, int tamanho, int estilo, Color cor) {
        JLabel label = new JLabel(valor);
        label.setFont(new Font("SansSerif", estilo, tamanho));
        label.setForeground(cor);
        return label;
    }

    /** Cria um botao com as cores e margens principais do portal. */
    private JButton botaoPrimario(String valor) {
        JButton botao = new JButton(valor);
        botao.setBackground(FOREST);
        botao.setForeground(SURFACE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(9, 13, 9, 13));
        return botao;
    }

    /** Substitui o conteudo atual pelo painel indicado. */
    private void trocarPagina(JPanel novaPagina) {
        removeAll();
        add(novaPagina, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
