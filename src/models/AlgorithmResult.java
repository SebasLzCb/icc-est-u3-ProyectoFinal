package models;

import java.util.List;

/**
 * Clase modelo para encapsular el resultado de la ejecución de un algoritmo.
 * Almacena el nombre del algoritmo, el tiempo de ejecución y la ruta encontrada.
 */
public class AlgorithmResult {
    private String algorithmName;
    private long executionTime; // En nanosegundos para mayor precisión
    private List<Cell> path;

    public AlgorithmResult(String algorithmName, long executionTime, List<Cell> path) {
        this.algorithmName = algorithmName;
        this.executionTime = executionTime;
        this.path = path;
    }

    // Getters
    public String getAlgorithmName() {
        return algorithmName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public List<Cell> getPath() {
        return path;
    }

    public int getPathLength() {
        return path != null ? path.size() : 0;
    }
}