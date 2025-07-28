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
     * Resuelve un laberinto mostrando una animación paso a paso.
     * @param stepCallback Una función que se llama en cada paso para actualizar la UI.
     * @return Una lista de Celdas (List<Cell>) con el camino.
     * @throws InterruptedException Si el hilo de la animación es interrumpido.
     */
    List<Cell> solveStepByStep(Cell[][] maze, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException;
}