package controllers;

import models.*;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;

import java.util.List;

public class MazeController {
    private Cell[][] mazeGrid; //va  a almacenar posiciones del laberinto
    private final SolveResults solveResults; //va a contener los resultados de los algoritmos

    private final MazeFrame mazeFrame;
    private final MazePanel mazePanel;
    private final ResultadosDialog resultadosDialog;

    public MazeController(MazeFrame mazeFrame, MazePanel mazePanel, ResultadosDialog resultadosDialog) {
        this.mazeFrame = mazeFrame;
        this.mazePanel = mazePanel;
        this.resultadosDialog = resultadosDialog;
        this.solveResults = new SolveResults();
    }

    public void setMaze(Cell[][] mazeGrid) {//va a leer las celdas del laberinto
        this.mazeGrid = mazeGrid;
        mazePanel.setMazeGrid(mazeGrid);
    }
    
    //ejecuta como se va a resolver los laberintos y guarda el respuesta
    public void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        AlgorithmResult result = new AlgorithmResult(algorithmName, executionTime, path);
        solveResults.addResult(result);
        mazePanel.showSolutionPath(path);
    }

    public void mostrarResultados() {
        resultadosDialog.setResults(solveResults.getResults()); //implementar en ResultadosDialog
        resultadosDialog.setVisible(true);
    }

    public List<AlgorithmResult> getResults() {//devuelve los resultados que estaban almacenados
        return solveResults.getResults();
    }

    public void clearResults() { //borra los resultados que staban almacenados 
        solveResults.clearResults();
        mazePanel.clearSolution();
    }

    public Cell[][] getMaze() {//retorna el laberinto
        return mazeGrid;
    }
}