package views;

import javax.swing.*;
import java.awt.*;
import models.Cell;
import models.CellState;

public class MazePanel extends JPanel {
    private Cell[][] mazeGrid;

    public MazePanel() {
        // El constructor puede estar vacío
    }

    public void setMazeGrid(Cell[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mazeGrid == null) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int numRows = mazeGrid.length;
        int numCols = mazeGrid[0].length;
        
        int cellWidth = panelWidth / numCols;
        int cellHeight = panelHeight / numRows;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                g.setColor(getColorForState(mazeGrid[row][col].getState()));
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                g.setColor(Color.BLACK); // Bordes negros para mejor contraste
                g.drawRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN DE COLORES! ---
    private Color getColorForState(CellState state) {
        switch (state) {
            case START: return Color.GREEN;
            case END: return Color.RED;
            case WALL: return Color.DARK_GRAY;
            case PATH: return Color.WHITE;
            case SOLUTION: return Color.BLUE;      // <-- CAMBIADO A AZUL
            case VISITED: return Color.LIGHT_GRAY; // <-- CAMBIADO A GRIS CLARO
            default: return Color.BLACK;
        }
    }
}