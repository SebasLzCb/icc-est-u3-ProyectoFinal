// src/views/MazeFrame.java

package views;

import javax.swing.*;
import java.awt.*;
import controllers.MazeController;

public class MazeFrame extends JFrame {

    private final MazePanel mazePanel;
    private final JButton bfsButton, dfsButton, recursiveBtButton, resultsButton, clearButton;

    public MazeFrame() {
        setTitle("Solucionador de Laberintos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Panel del Laberinto (Centro)
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // 2. Panel de Controles (Sur)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        bfsButton = new JButton("Resolver con BFS");
        dfsButton = new JButton("Resolver con DFS");
        recursiveBtButton = new JButton("Resolver con Backtracking");
        resultsButton = new JButton("Ver Resultados");
        clearButton = new JButton("Limpiar Solución");

        controlPanel.add(new JLabel("Algoritmos:"));
        controlPanel.add(bfsButton);
        controlPanel.add(dfsButton);
        controlPanel.add(recursiveBtButton);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(resultsButton);
        controlPanel.add(clearButton);
        
        add(controlPanel, BorderLayout.SOUTH);

        pack(); // Ajusta el tamaño de la ventana a los componentes
        setLocationRelativeTo(null); // Centra la ventana
        setVisible(true);
    }

    // --- Métodos para que el controlador acceda a los componentes ---

    public MazePanel getMazePanel() {
        return mazePanel;
    }

    public JButton getBfsButton() {
        return bfsButton;
    }

    public JButton getDfsButton() {
        return dfsButton;
    }
    
    public JButton getRecursiveBtButton() {
        return recursiveBtButton;
    }

    public JButton getResultsButton() {
        return resultsButton;
    }
    
    public JButton getClearButton() {
        return clearButton;
    }
}
