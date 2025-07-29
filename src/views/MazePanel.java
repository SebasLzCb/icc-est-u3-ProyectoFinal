package views;

import javax.swing.*;
import java.awt.*;
import models.Cell;
import models.CellState;

/**
 * Representa el panel de dibujo donde se visualiza el laberinto.
 * Esta clase es una parte de la "Vista" en el patrón MVC. Su única responsabilidad
 * es pintar el estado actual del laberinto basándose en la matriz de celdas (mazeGrid).
 */
public class MazePanel extends JPanel {
    // La matriz de celdas que representa el estado actual del laberinto.
    private Cell[][] mazeGrid;

    /**
     * Constructor del panel del laberinto.
     */
    public MazePanel() {
        // El constructor no necesita inicializar nada, ya que el controlador
        // se encargará de configurar el laberinto.
    }

    /**
     * Establece la matriz del laberinto que se va a dibujar y refresca el panel.
     * @param mazeGrid La matriz 2D de celdas a visualizar.
     */
    public void setMazeGrid(Cell[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        revalidate(); // Notifica al layout manager que el componente podría haber cambiado de tamaño.
        repaint();    // Llama a paintComponent() para redibujar el panel con el nuevo laberinto.
    }
    
    /**
     * Este es el método principal de dibujado de Swing. Se llama automáticamente
     * cada vez que el panel necesita ser redibujado (por ejemplo, al llamar a repaint()).
     * @param g El contexto gráfico proporcionado por Swing para dibujar.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Llama al método de la clase padre para limpiar el panel.
        if (mazeGrid == null) return; // Si no hay laberinto, no hay nada que dibujar.

        // --- Lógica para el Dibujado Adaptativo ---
        // Se calcula el tamaño de cada celda dinámicamente basándose en el tamaño actual del panel.
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int numRows = mazeGrid.length;
        int numCols = mazeGrid[0].length;
        
        // El tamaño de cada celda es el espacio total dividido por el número de celdas.
        int cellWidth = panelWidth / numCols;
        int cellHeight = panelHeight / numRows;

        // Se recorre la matriz celda por celda para dibujarla.
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Se obtiene el color correspondiente al estado de la celda actual.
                g.setColor(getColorForState(mazeGrid[row][col].getState()));
                // Se dibuja un rectángulo relleno con ese color.
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                // Se dibuja el borde de la celda en negro para una mejor visibilidad.
                g.setColor(Color.BLACK);
                g.drawRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    /**
     * Devuelve un color específico basado en el estado de una celda.
     * @param state El estado de la celda (START, END, WALL, etc.).
     * @return El objeto Color correspondiente para dibujar.
     */
    private Color getColorForState(CellState state) {
        switch (state) {
            case START: return Color.GREEN;
            case END: return Color.RED;
            case WALL: return Color.DARK_GRAY;
            case PATH: return Color.WHITE;
            case SOLUTION: return Color.BLUE;      // El camino de la solución final.
            case VISITED: return Color.LIGHT_GRAY; // Las celdas exploradas durante la animación.
            default: return Color.BLACK;        // Color por defecto en caso de un estado desconocido.
        }
    }
}