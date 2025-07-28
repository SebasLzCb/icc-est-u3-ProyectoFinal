// src/views/MazePanel.java
package views;

import javax.swing.*;
import java.awt.*;
import models.Cell;
import models.CellState;

public class MazePanel extends JPanel {

    private static final int CELL_SIZE = 25;
    private Cell[][] mazeGrid;

    public MazePanel() {
        // El panel de dibujo es pasivo, solo responde a las órdenes
        // del controlador. Aquí se añadirán los listeners.
    }

    public void setMazeGrid(Cell[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        int width = mazeGrid[0].length * CELL_SIZE;
        int height = mazeGrid.length * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        revalidate(); // Avisa al layout manager que el tamaño ha cambiado
        repaint();
    }
    
 // En src/views/MazePanel.java

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (mazeGrid == null) return;

    // --- ¡ESTA ES LA LÓGICA CLAVE! ---
    // Calcula el tamaño de cada celda dinámicamente
    int panelWidth = getWidth();
    int panelHeight = getHeight();
    int numRows = mazeGrid.length;
    int numCols = mazeGrid[0].length;
    
    // El tamaño de la celda es el espacio disponible dividido por el número de celdas
    int cellWidth = panelWidth / numCols;
    int cellHeight = panelHeight / numRows;

    // Dibuja el laberinto usando el tamaño calculado
    for (int row = 0; row < numRows; row++) {
        for (int col = 0; col < numCols; col++) {
            g.setColor(getColorForState(mazeGrid[row][col].getState()));
            g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            g.setColor(Color.LIGHT_GRAY); // Un color más suave para los bordes
            g.drawRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
        }
    }
}

    private Color getColorForState(CellState state) {
        switch (state) {
            case START: return Color.GREEN;
            case END: return Color.RED;
            case WALL: return Color.DARK_GRAY;
            case PATH: return Color.WHITE;
            case SOLUTION: return Color.CYAN;
            case VISITED: return Color.YELLOW; // Para el modo paso a paso
            default: return Color.BLACK;
        }
    }
    
    public int getCellSize() {
        return CELL_SIZE;
    }
}