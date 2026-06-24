package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Validacao{
    private BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
    
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
                // Validar: apenas letras e espaços
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
                // Validação simples de email
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

}
