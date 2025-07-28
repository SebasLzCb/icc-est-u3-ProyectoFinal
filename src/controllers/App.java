package controllers;

// src/App.java
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import views.MazeFrame;

public class App {

    public static void main(String[] args) {
        // Iniciar la aplicación en el hilo de despacho de eventos de Swing.
        SwingUtilities.invokeLater(() -> {
            
            // 1. Pedir las dimensiones al usuario
            int rows = 0, cols = 0;
            while (rows <= 0 || cols <= 0) {
                try {
                    String rowsStr = JOptionPane.showInputDialog(null, "Ingrese el número de filas:", "Dimensiones del Laberinto", JOptionPane.QUESTION_MESSAGE);
                    if (rowsStr == null) System.exit(0); // El usuario cerró el diálogo

                    String colsStr = JOptionPane.showInputDialog(null, "Ingrese el número de columnas:", "Dimensiones del Laberinto", JOptionPane.QUESTION_MESSAGE);
                    if (colsStr == null) System.exit(0); // El usuario cerró el diálogo

                    rows = Integer.parseInt(rowsStr);
                    cols = Integer.parseInt(colsStr);

                    if (rows <= 0 || cols <= 0) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese números positivos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingrese solo números.", "Error", JOptionPane.ERROR_MESSAGE);
                    rows = 0; // Reset para que el bucle continúe
                    cols = 0;
                }
            }

            // 2. Una vez obtenidas las dimensiones, crear y mostrar la aplicación
            MazeFrame mazeFrame = new MazeFrame();
            MazeController controller = new MazeController(mazeFrame, rows, cols);
            
            mazeFrame.setVisible(true);

            
        });
    }
}