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
    
    public int validarNumero(String msg){
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
                }
            }
            catch (IOException e) {
                System.out.println("Erro de leitura. Tente novamente.");
            }
        }
        while(texto == null || texto.isEmpty());
        return texto;
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
                System.out.println("[HH:mm]: ");
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
