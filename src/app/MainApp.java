/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import service.TaskService;
import ui.MainFrame;

/**
 *
 * @author pame
 */
public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TaskService service = new TaskService();
                MainFrame frame = new MainFrame(service);
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error iniciando la aplicación: " + ex.getMessage());
            }
        });
    }
}
