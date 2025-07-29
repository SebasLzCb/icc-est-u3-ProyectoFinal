package solver;

import java.util.List;
import java.util.function.Consumer;
import models.Cell;

/**
 * Define el contrato (las reglas) para cualquier clase que quiera ser un
 * solucionador de laberintos en este proyecto.
 */
public interface MazeSolver {

    /**
     * Resuelve un laberinto de forma rápida, sin animación.
     * @return Una lista de Celdas (List<Cell>) con el camino.
     */
    List<Cell> solve(Cell[][] maze, Cell start, Cell end);

    /**
     * Inicializa el algoritmo para una ejecución paso a paso.
     * Esto prepara el estado interno del solucionador para que pueda avanzar celda por celda.
     * @param maze La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     */
    void initializeStepByStep(Cell[][] maze, Cell start, Cell end);

    /**
     * Ejecuta un solo paso del algoritmo.
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    Cell doStep();

    /**
     * Verifica si el algoritmo ha terminado su búsqueda (encontró el final o agotó todas las opciones).
     * @return true si la búsqueda paso a paso ha finalizado, false en caso contrario.
     */
    boolean isStepByStepFinished();

    /**
     * Devuelve el camino final encontrado después de que la búsqueda paso a paso haya terminado.
     * @return Una lista de Celdas (List<Cell>) con el camino final, o una lista vacía si no se encontró.
     */
    List<Cell> getFinalPath();
}