package Presentacion;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class main 
{
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> {
            try 
            {
                // Instanciamos el controlador principal
                ctrlPresentacion ctrl = new ctrlPresentacion();
                // Iniciamos la UI
                ctrl.inicializarPresentacion();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error crítico: " + e.getMessage());
            }
        });
    }
}