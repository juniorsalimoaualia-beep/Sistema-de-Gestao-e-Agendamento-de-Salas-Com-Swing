package com.mycompany.sistemadegestaoeagendamentodesalas.view;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Validacao{
    private BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
    private static final Pattern NOME_PATTERN = Pattern.compile("[\\p{L}\\p{M}][\\p{L}\\p{M} .'-]*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final DateTimeFormatter HORA_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    
    /** Le um numero inteiro e repete a solicitacao enquanto a entrada for invalida. */
    public int validarInt(String msg){
        int numero;
        while(true){
            try{
                System.out.println(msg);
                numero = Integer.parseInt(br.readLine());
                return numero;
            }catch(IOException e){
                System.out.println("Erro: Digite um numero inteiro valido. " + e.getMessage());
            }catch(NumberFormatException e){
                System.out.println("Erro: Digite um numero inteiro valido.");
            }
        }
    }

    /** Le e valida um numero de celular dentro do intervalo aceite. */
    public int validarCell(String msg){
        int numero;
        while(true){
            try {
                System.out.println(msg);
                numero=Integer.parseInt(br.readLine());
                if(!(numero>=820000000 && numero<=879999999)){
                    System.out.println("Numero invalido!");
                }
                else{return numero;}
            } catch (Exception e) {
                System.out.println("Digite um numero valido. "+e.getMessage());
            }
        }
    }

    /** Le texto nao vazio composto apenas por letras e espacos. */
    public String validarString(String msg){
        String texto = null;
        do{
            try {
                System.out.println(msg);
                texto = br.readLine();
                if(texto != null){
                    texto = texto.trim();
                }
                if(texto == null || texto.isEmpty()){
                    System.out.println("Erro: Campo nao deve ser vazio!");
                    continue;
                }
                // Validar: apenas letras e espacos
                if(!texto.matches("^[a-zA-Záàâäãéèêëíìîïóòôöõúùûüçñ\\s]+$")){
                    System.out.println("Erro: Campo deve conter apenas letras e espaços. Numeros, pontuacao e simbolos nao sao permitidos.");
                    texto = null;
                    continue;
                }
            }
            catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
        }
        while(texto == null || texto.isEmpty());
        return texto;
    }

    /** Le um nome de sala composto por letras e numeros. */
    public String validarSala(String msg) {
        String texto = null;
        do {
            try {
                System.out.println(msg);
                texto = br.readLine();
                if (texto != null) {
                    texto = texto.trim();
                }
                if (texto == null || texto.isEmpty()) {
                    System.out.println("Erro: Campo nao deve ser vazio!");
                    continue;
                }
                // Validar: apenas letras e numeros, sem espacos ou simbolos
                if (!texto.matches("^[a-zA-Z0-9]+$")) {
                    System.out.println("Erro: Nome de sala deve conter apenas letras e numeros, sem espacos ou simbolos.");
                    texto = null;
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
        } while (texto == null || texto.isEmpty());
        return texto;
    }

    /** Le e valida um endereco de e-mail. */
    public String validarEmail(String msg){
        String email = null;
        do{
            try {
                System.out.println(msg);
                email = br.readLine();
                if(email != null){
                    email = email.trim();
                }
                if(email == null || email.isEmpty()){
                    System.out.println("Erro: Email nao deve ser vazio!");
                    continue;
                }
                // Validacao simples de email
                if(!email.matches("^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                    System.out.println("Erro: Email invalido. Use o formato: usuario@dominio.com");
                    email = null;
                    continue;
                }
            }
            catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
        }
        while(email == null || email.isEmpty());
        return email;
    }

    /** Le uma senha nao vazia. */
    public String validarSenha(String msg){
        String senha = null;
        do{
            try {
                System.out.println(msg);
                senha = br.readLine();
                if(senha != null){
                    senha = senha.trim();
                }
                if(senha == null || senha.isEmpty()){
                    System.out.println("Erro: Senha nao deve ser vazia!");
                    continue;
                }
            }
            catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
        }
        while(senha == null || senha.isEmpty());
        return senha;
    }

    /** Le uma data no formato dia/mes/ano. */
    public LocalDate validarDate(String msg){
        DateTimeFormatter fmt= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while(true){
            try {
                System.out.println(msg+" [dd/MM/yyyy]: ");
                String entrada = br.readLine();
                return LocalDate.parse(entrada,fmt);
            } catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
            catch(DateTimeException dt){System.out.println("Erro: Data invalida. Use o formato dd/MM/yyyy.");}
        }    
    }

    /** Le uma hora valida no formato de 24 horas. */
    public LocalTime validarTime(String msg){
        DateTimeFormatter fmt= DateTimeFormatter.ofPattern("HH:mm");
        while(true){
            try {
                System.out.println("Hora:minuto : ");
                String entrada= br.readLine().trim();
                return LocalTime.parse(entrada,fmt);
            } catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
            catch(DateTimeException dt){System.out.println("Erro: Hora invalida. Use o formato 24h, ex: 17:05.");}
        }
    }

    /** Le as horas inicial e final e garante que a final seja posterior. */
    public LocalTime[] validarPeriodo(String msgInicio, String msgFim){
        while(true){
            LocalTime inicio= validarTime(msgInicio);
            LocalTime fim= validarTime(msgFim);
            if(fim.isAfter(inicio)){
                return new LocalTime[]{inicio,fim};
            }
            else{System.out.println("Erro: Hora fim deve ser depois da hora inicio.");}
        }
    }

    /** Le e valida uma opcao de enumeracao apresentada ao utilizador. */
    public <T extends Enum<T>> T validarReserva(String msg, Class<T> enumClass){
        while(true){
            System.out.println(msg);
            System.out.println("Opcoes: ");
            for(T opcao: enumClass.getEnumConstants()){
                System.out.println(opcao.name()+" ");
            }
            System.out.println("\n> ");
            
            try {
                String entrada= br.readLine().trim().toUpperCase();
                return Enum.valueOf(enumClass, entrada);
            } catch (Exception e) {
                System.out.println("Erro: Opcao invalida. Digite uma das opcoes listadas.");
            }
        }
    }

    /** Valida um texto obrigatorio, com limite e pelo menos uma letra. */
    public static String validarTextoObrigatorio(String valor, String campo, int limite) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.isEmpty() || texto.length() > limite
                || texto.codePoints().anyMatch(Character::isISOControl)
                || texto.codePoints().noneMatch(Character::isLetter)) {
            throw new IllegalArgumentException("Informe um valor valido para " + campo + ".");
        }
        return texto;
    }

    /** Valida um nome de pessoa com letras, espacos, apostrofos e hifens. */
    public static String validarNomePessoa(String valor, String campo) {
        String texto = validarTextoObrigatorio(valor, campo, 100);
        if (!NOME_PATTERN.matcher(texto).matches()) {
            throw new IllegalArgumentException(campo + " deve conter apenas letras e separadores validos.");
        }
        return texto;
    }

    /** Converte um campo numerico positivo dentro dos limites indicados. */
    public static int validarInteiro(String valor, String campo, int minimo, int maximo) {
        String texto = valor == null ? "" : valor.trim();
        if (!texto.matches("[0-9]+")) {
            throw new IllegalArgumentException("Informe apenas numeros para " + campo + ".");
        }
        try {
            int numero = Integer.parseInt(texto);
            if (numero < minimo || numero > maximo) {
                throw new IllegalArgumentException(campo + " deve estar entre " + minimo + " e " + maximo + ".");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O valor de " + campo + " e demasiado grande.");
        }
    }

    /** Valida um celular mocambicano dentro do intervalo usado pelo projecto. */
    public static int validarCelular(String valor) {
        return validarInteiro(valor, "celular", 820000000, 879999999);
    }

    /** Valida o formato e o tamanho maximo do endereco de e-mail. */
    public static String validarEnderecoEmail(String valor) {
        String email = valor == null ? "" : valor.trim();
        if (email.length() > 190 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Informe um endereco de e-mail valido.");
        }
        return email;
    }

    /** Converte uma data ISO valida no formato AAAA-MM-DD. */
    public static LocalDate validarDataIso(String valor) {
        String texto = valor == null ? "" : valor.trim();
        try {
            if (!texto.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                throw new DateTimeParseException("Formato invalido", texto, 0);
            }
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use uma data valida no formato AAAA-MM-DD.");
        }
    }

    /** Converte uma hora valida no formato de 24 horas HH:MM. */
    public static LocalTime validarHora(String valor, String campo) {
        String texto = valor == null ? "" : valor.trim();
        try {
            if (!texto.matches("[0-9]{2}:[0-9]{2}")) {
                throw new DateTimeParseException("Formato invalido", texto, 0);
            }
            return LocalTime.parse(texto, HORA_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Informe " + campo + " no formato HH:MM.");
        }
    }

    /** Instala um filtro que aceita apenas digitos e limita o tamanho do campo. */
    public static void filtrarDigitos(JTextField campo, int limite) {
        instalarFiltro(campo, "[0-9]*", limite);
    }

    /** Instala um filtro de letras valido para nomes pessoais. */
    public static void filtrarNomePessoa(JTextField campo) {
        instalarFiltro(campo, "[\\p{L}\\p{M} .'-]*", 100);
    }

    /** Instala um filtro textual para nomes de cursos, salas e disciplinas. */
    public static void filtrarTexto(JTextField campo, int limite) {
        instalarFiltro(campo, "[\\p{L}\\p{M}\\p{N} .,'()&/+#-]*", limite);
    }

    /** Instala um filtro para campos de data ISO ou hora HH:MM. */
    public static void filtrarDataHora(JTextField campo, boolean data) {
        instalarFiltro(campo, data ? "[0-9-]*" : "[0-9:]*", data ? 10 : 5);
    }

    /** Liga um filtro de caracteres e comprimento a um campo de texto. */
    private static void instalarFiltro(JTextField campo, String permitido, int limite) {
        if (!(campo.getDocument() instanceof AbstractDocument documento)) {
            throw new IllegalArgumentException("O campo nao permite validacao de entrada.");
        }
        Pattern padrao = Pattern.compile(permitido);
        documento.setDocumentFilter(new DocumentFilter() {
            /** Aceita apenas texto permitido sem ultrapassar o tamanho definido. */
            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                    throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String substituto = texto == null ? "" : texto;
                String proximo = atual.substring(0, offset) + substituto + atual.substring(offset + length);
                if (proximo.length() <= limite && padrao.matcher(proximo).matches()) {
                    fb.replace(offset, length, substituto, attrs);
                }
            }

            /** Encaminha a insercao pelo filtro de caracteres e tamanho. */
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, texto, attrs);
            }
        });
    }
}
