package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.ui;

public class MenuUI {
    private Validacao vd;
    private UsuarioUI us;
    public void menu(){
        vd = new Validacao();
        us = new UsuarioUI();
        while(true){
            System.out.println("\nSelecione a opcao que deseja:");
            System.out.println("1. Admin");
            System.out.println("2. Docente");
            System.out.println("3. Estudante");
            System.out.println("4. Secretario");
            System.out.println("5. Departamento");
            System.out.println("0. Sair");
            int opcao = vd.validarNumero("Digite a opcao:");
            if(opcao == 0){
                System.out.println("Saindo do sistema...");
                break;
            }
            us.login(opcao);
        }
    }
}
