package models;

import java.util.Collections;
import java.util.List;

/**
 * Clase modelo para encapsular el resultado de la ejecución de un algoritmo.
 * Almacena el nombre del algoritmo, el tiempo de ejecución y la ruta encontrada.
 */
public class AlgorithmResult {
    private String algorithmName;
    private long executionTime; // En nanosegundos para mayor precisión
    private List<Cell> pathCells; // La lista real de celdas del camino (usada para la visualización)
    private int pathLength;     // La longitud del camino (número de celdas), almacenada explícitamente

    // Constructor principal, usado cuando se resuelve el laberinto y se tiene la lista de celdas.
    public AlgorithmResult(String algorithmName, long executionTime, List<Cell> pathCells) {
        this.algorithmName = algorithmName;
        this.executionTime = executionTime;
        this.pathCells = pathCells;
        this.pathLength = (pathCells != null) ? pathCells.size() : 0;
    }

    // Nuevo constructor, usado al cargar resultados del DAO (cuando solo se tiene la longitud, no las celdas individuales).
    public AlgorithmResult(String algorithmName, long executionTime, int pathLength) {
        this.algorithmName = algorithmName;
        this.executionTime = executionTime;
        this.pathLength = pathLength;
        this.pathCells = Collections.emptyList(); // La lista de celdas no se recupera del CSV
    }

    // Getters
    public String getAlgorithmName() {
        return algorithmName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    // Este getter ahora devuelve la lista de celdas del camino.
    public List<Cell> getPath() {
        return pathCells;
    }

    // Este getter ahora devuelve la longitud almacenada explícitamente.
    // Es lo que se usará para mostrar en la tabla y guardar en el CSV.
    public int getPathLength() {
        return pathLength;
    }
}