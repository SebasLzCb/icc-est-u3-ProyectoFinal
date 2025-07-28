package views;

import javax.swing.*;
import java.awt.*;
import java.util.List; // Se puede quitar si no se usa en otro lado
import models.Cell;
import models.CellState;

public class MazePanel extends JPanel {

    private static final int CELL_SIZE = 25;
    private Cell[][] mazeGrid;

    public MazePanel() {
        // El constructor está bien como estaba
    }

    public void setMazeGrid(Cell[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        int width = mazeGrid[0].length * CELL_SIZE;
        int height = mazeGrid.length * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        repaint();
    }
    
    // Este método ya no es necesario, el controlador se encargará de cambiar los estados
    // public void showSolutionPath(List<Cell> path) { ... }
    
    // Este método ya no es necesario, el controlador lo manejará
    // public void clearSolution() { ... }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mazeGrid == null) return;

        // El panel simplemente dibuja el estado actual del grid.
        for (int row = 0; row < mazeGrid.length; row++) {
            for (int col = 0; col < mazeGrid[0].length; col++) {
                // Obtiene el color directamente del estado de la celda.
                g.setColor(getColorForState(mazeGrid[row][col].getState()));
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private Color getColorForState(CellState state) {
        switch (state) {
            case START: return Color.GREEN;
            case END: return Color.RED;
            case WALL: return Color.DARK_GRAY;
            case PATH: return Color.WHITE; // Caminos normales son blancos
            case SOLUTION: return Color.CYAN; // El camino de la solución será cian
            // El estado VISITED ya no se usará para dibujar, pero lo dejamos por si acaso.
            case VISITED: return Color.LIGHT_GRAY; 
            default: return Color.BLACK; // Si algo sale mal, que se vea negro
        }
    }
}
