package controllers;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import views.MazeFrame;


/**
 * Clase principal que inicia la aplicación del solucionador de laberintos.
 * Su única responsabilidad es configurar y lanzar la interfaz gráfica de usuario (GUI).
 */
public class App {

    /**
     * El punto de entrada de la aplicación.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        // SwingUtilities.invokeLater asegura que todo el código de la GUI se ejecute
        // en el hilo de despacho de eventos (EDT), lo cual es una práctica segura en Swing.
        SwingUtilities.invokeLater(() -> {
            
            // --- 1. Pedir las dimensiones del laberinto al usuario ---
            int rows = 0, cols = 0;
            
            // Bucle para asegurar que el usuario ingrese dimensiones válidas (números positivos).
            while (rows <= 0 || cols <= 0) {
                try {
                    // Muestra un diálogo para que el usuario ingrese el número de filas.
                    String rowsStr = JOptionPane.showInputDialog(null, "Ingrese el número de filas:", "Dimensiones del Laberinto", JOptionPane.QUESTION_MESSAGE);
                    // Si el usuario cierra el diálogo (presiona cancelar o la 'X'), la aplicación termina.
                    if (rowsStr == null) System.exit(0);

                    // Muestra un diálogo para que el usuario ingrese el número de columnas.
                    String colsStr = JOptionPane.showInputDialog(null, "Ingrese el número de columnas:", "Dimensiones del Laberinto", JOptionPane.QUESTION_MESSAGE);
                    if (colsStr == null) System.exit(0);

                    // Convierte el texto ingresado a números enteros.
                    rows = Integer.parseInt(rowsStr);
                    cols = Integer.parseInt(colsStr);

                    // Valida que los números no sean cero o negativos.
                    if (rows <= 0 || cols <= 0) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese números positivos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    // Si el usuario ingresa texto que no es un número, muestra un error.
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingrese solo números.", "Error", JOptionPane.ERROR_MESSAGE);
                    rows = 0; // Resetea las filas para que el bucle `while` continúe.
                    cols = 0; // Resetea las columnas.
                }
            }

            // --- 2. Crear y mostrar la aplicación ---
            // Una vez que tenemos dimensiones válidas, creamos los componentes del patrón MVC.
            
            // Se crea la Vista (la ventana principal).
            MazeFrame mazeFrame = new MazeFrame();
            
            // Se crea el Controlador, pasándole la vista y las dimensiones del laberinto.
            MazeController controller = new MazeController(mazeFrame, rows, cols);
            
            // Se hace visible la ventana principal para el usuario.
            mazeFrame.setVisible(true);
        });
    }
}