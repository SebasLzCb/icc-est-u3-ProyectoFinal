package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando un enfoque recursivo en 4 direcciones.
 * Esta implementación es una forma de Búsqueda en Profundidad (DFS) que no utiliza
 * backtracking explícito (no elimina celdas del camino si falla).
 * El camino se construye hacia atrás una vez que se encuentra la solución.
 */
public class MazeSolverRecursivoCompleto implements MazeSolver {

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     * @param maze La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de celdas que forman el camino. Retorna una lista vacía si no hay solución.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        if (findPath(maze, start, end, visited, path)) {
            // El camino se construye desde el final hasta el inicio, por lo que es necesario invertirlo.
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada para encontrar el camino.
     * @return true si se encuentra un camino desde 'current' hasta 'end', false de lo contrario.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();
        
        // --- Condición de Parada (Caso Base) ---
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        visited[row][col] = true;

        // --- Condición de Éxito (Caso Base) ---
        if (current.equals(end)) {
            path.add(current); // Añadir la celda final al camino y propagar el éxito.
            return true;
        }
        
        // --- Paso Recursivo ---
        // Se define el orden de exploración de los vecinos para coincidir con el del profesor:
        // Prioridad: Abajo, Derecha, Arriba, Izquierda.
        int[] dRow = {1, 0, -1, 0};
        int[] dCol = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                // Se llama recursivamente a la función para el vecino.
                if (findPath(maze, maze[newRow][newCol], end, visited, path)) {
                    // Si el vecino encontró un camino, se añade la celda actual al camino.
                    path.add(current);
                    return true;
                }
            }
        }
        // Si ninguno de los vecinos encontró un camino, esta ruta no es válida.
        return false;
    }

    /**
     * Resuelve el laberinto mostrando una animación paso a paso.
     */
    @Override
    public List<Cell> solveStepByStep(Cell[][] maze, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        if (findPathStepByStep(maze, start, end, visited, path, stepCallback)) {
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Versión animada de la función recursiva para encontrar el camino.
     */
    private boolean findPathStepByStep(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path, Consumer<Cell> stepCallback) throws InterruptedException {
        int row = current.getRow();
        int col = current.getCol();
        
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        visited[row][col] = true;
        stepCallback.accept(current); // Publica la celda actual para que la UI la pinte.
        Thread.sleep(25);             // Pequeña pausa para que la animación sea visible.

        if (current.equals(end)) {
            path.add(current);
            return true;
        }

        // Se mantiene el mismo orden de exploración que en la versión no animada.
        int[] dRow = {1, 0, -1, 0};
        int[] dCol = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                if (findPathStepByStep(maze, maze[newRow][newCol], end, visited, path, stepCallback)) {
                    path.add(current);
                    return true;
                }
            }
        }
        return false;
    }
}