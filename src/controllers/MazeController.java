package controllers;

import models.*;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;

import java.util.List;

public class MazeController {
    private Cell[][] mazeGrid;
    private final SolveResults solveResults;

    private final MazeFrame mazeFrame;
    private final MazePanel mazePanel;
    private final ResultadosDialog resultadosDialog;

    public MazeController(MazeFrame mazeFrame, MazePanel mazePanel, ResultadosDialog resultadosDialog) {
        this.mazeFrame = mazeFrame;
        this.mazePanel = mazePanel;
        this.resultadosDialog = resultadosDialog;
        this.solveResults = new SolveResults();
    }

    public void setMaze(Cell[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        mazePanel.setMazeGrid(mazeGrid);
    }

    /**
     * ¡LÓGICA CORREGIDA!
     * Añade un resultado a la lista. Si ya existe un resultado para el mismo
     * algoritmo, lo reemplaza. Esto permite acumular resultados en la tabla.
     */
    public void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        // Primero, limpia el dibujo de la solución anterior del panel.
        clearVisualPath();

        // Elimina el resultado anterior para este algoritmo específico, si existe.
        // Esto evita tener duplicados en la tabla si se ejecuta un algoritmo varias veces.
        solveResults.getResults().removeIf(res -> res.getAlgorithmName().equals(algorithmName));

        // Crea y añade el nuevo resultado a la lista acumulativa.
        AlgorithmResult result = new AlgorithmResult(algorithmName, executionTime, path);
        solveResults.addResult(result);

        // Dibuja el nuevo camino en el laberinto.
        for (Cell cell : path) {
            if (cell.getState() == CellState.PATH) { // No sobreescribir START o END
                cell.setState(CellState.SOLUTION);
            }
        }
        mazePanel.repaint(); // Pide al panel que se redibuje.
    }

    public void mostrarResultados() {
        resultadosDialog.setResults(solveResults.getResults());
        resultadosDialog.setVisible(true);
    }

    /**
     * Limpia TODOS los resultados (datos y dibujo).
     * Esta función es para el botón "Limpiar".
     */
    public void clearResults() {
        solveResults.clearResults();
        clearVisualPath();
    }
    
    /**
     * Limpia solo el DIBUJO de la solución en el laberinto,
     * pero mantiene los datos en la tabla.
     */
    private void clearVisualPath() {
        if (mazeGrid != null) {
            for (int row = 0; row < mazeGrid.length; row++) {
                for (int col = 0; col < mazeGrid[0].length; col++) {
                    if (mazeGrid[row][col].getState() == CellState.SOLUTION) {
                        mazeGrid[row][col].setState(CellState.PATH);
                    }
                }
            }
        }
        // No es necesario llamar a repaint aquí, addAlgoritmoResult lo hará.
    }


    public Cell[][] getMaze() {
        return mazeGrid;
    }
}