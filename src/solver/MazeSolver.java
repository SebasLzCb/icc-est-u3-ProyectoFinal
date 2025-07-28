package solver;

import java.util.List;
import models.Cell;

/**
 * Define el contrato (las reglas) para cualquier clase que quiera ser un
 * solucionador de laberintos en este proyecto.
 * Todas las clases de algoritmos (BFS, DFS, etc.) deben implementar esta interfaz.
 */
public interface MazeSolver {

    /**
     * Resuelve un laberinto desde un punto de inicio a un punto de fin.
     *
     * @param maze La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de Celdas (List<Cell>) que representa el camino encontrado.
     * Si no se encuentra una solución, debe devolver una lista vacía.
     */
    List<Cell> solve(Cell[][] maze, Cell start, Cell end);

}