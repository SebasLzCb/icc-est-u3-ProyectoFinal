package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor para almacenar una colección de resultados de diferentes algoritmos
 * ejecutados sobre el mismo laberinto.
 */
public class SolveResults {
    private final List<AlgorithmResult> results;

    public SolveResults() {
        this.results = new ArrayList<>();
    }

    /**
     * Agrega el resultado de un nuevo algoritmo a la lista.
     * @param result El resultado a agregar.
     */
    public void addResult(AlgorithmResult result) {
        this.results.add(result);
    }

    /**
     * Devuelve la lista de todos los resultados almacenados.
     * @return Una lista de objetos AlgorithmResult.
     */
    public List<AlgorithmResult> getResults() {
        return results;
    }

    /**
     * Limpia la lista de resultados, útil para iniciar una nueva simulación.
     */
    public void clearResults() {
        this.results.clear();
    }
}