package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto usando un enfoque recursivo simple en 2 direcciones (derecha y abajo).
 * Esta versión no utiliza backtracking explícito, construyendo el camino solo al encontrar
 * una ruta exitosa, para replicar un comportamiento visual específico.
 */
public class MazeSolverRecursivo implements MazeSolver {

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        if (findPath(maze, start, end, visited, path)) {
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada que busca el camino.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        visited[row][col] = true;

        if (current == end) {
            path.add(current);
            return true;
        }

        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN CLAVE! ---
        // 1. Explorar derecha PRIMERO (solo si no estamos en el borde derecho)
        if (col + 1 < maze[0].length) {
            if (findPath(maze, maze[row][col + 1], end, visited, path)) {
                path.add(current);
                return true;
            }
        }
        // 2. Explorar abajo DESPUÉS (solo si no estamos en el borde inferior)
        if (row + 1 < maze.length) {
            if (findPath(maze, maze[row + 1][col], end, visited, path)) {
                path.add(current);
                return true;
            }
        }
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
     * Versión animada de la función recursiva.
     */
    private boolean findPathStepByStep(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path, Consumer<Cell> stepCallback) throws InterruptedException {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        visited[row][col] = true;
        
        stepCallback.accept(current);
        Thread.sleep(50);

        if (current == end) {
            path.add(current);
            return true;
        }
        
        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN CLAVE! ---
        if (col + 1 < maze[0].length) {
            if (findPathStepByStep(maze, maze[row][col + 1], end, visited, path, stepCallback)) {
                path.add(current);
                return true;
            }
        }
        if (row + 1 < maze.length) {
            if (findPathStepByStep(maze, maze[row + 1][col], end, visited, path, stepCallback)) {
                path.add(current);
                return true;
            }
        }
        return false;
    }
}