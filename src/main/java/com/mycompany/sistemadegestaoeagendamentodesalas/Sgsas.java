package com.mycompany.sistemadegestaoeagendamentodesalas;

import javax.swing.SwingUtilities;
import com.mycompany.sistemadegestaoeagendamentodesalas.view.MenuUI;

public class Sgsas {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuUI().setVisible(true));
    }
}