package controllers;

import models.*;

import java.util.List;

public class MazeController {
    private Cell[][] mazeGrid; //va  a almacenar posiciones del laberinto
    private SolveResults solveResults; //va a contener los resultados de los algoritmos

    public MazeController() {
        this.solveResults = new SolveResults();
    }

    public void setMaze(Cell[][] mazeGrid) {//va a leer las celdas del laberinto
        this.mazeGrid = mazeGrid;
    }
    
    //ejecuta como se va a resolver los laberintos y guarda el respuesta
    public void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        AlgorithmResult result = new AlgorithmResult(algorithmName, executionTime, path);
        solveResults.addResult(result);
    }

    public List<AlgorithmResult> getResults() {//devuelve los resultados que estaban almacenados
        return solveResults.getResults();
    }

    public void clearResults() { //borra los resultados que staban almacenados 
        solveResults.clearResults();
    }

    public Cell[][] getMaze() {//retorna el laberinto
        return mazeGrid;
    }
}
