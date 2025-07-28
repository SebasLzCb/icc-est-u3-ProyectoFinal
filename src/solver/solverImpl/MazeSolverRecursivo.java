package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto usando un enfoque recursivo simple en 2 direcciones
 * (derecha y abajo). Esta versión NO usa backtracking explícito, construyendo
 * el camino solo al encontrar una ruta exitosa, para replicar el comportamiento visual deseado.
 */
public class MazeSolverRecursivo implements MazeSolver {

    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        if (findPath(maze, start, end, visited, path)) {
            Collections.reverse(path); // El camino se construye al revés
            return path;
        }
        return Collections.emptyList();
    }

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

        // --- LÓGICA CORREGIDA (SIN BACKTRACKING) ---
        // 1. Explorar derecha primero
        if (col + 1 < maze[0].length) {
            if (findPath(maze, maze[row][col + 1], end, visited, path)) {
                path.add(current); // Solo añade al camino si la ruta tuvo éxito
                return true;
            }
        }
        // 2. Explorar abajo después
        if (row + 1 < maze.length) {
            if (findPath(maze, maze[row + 1][col], end, visited, path)) {
                path.add(current); // Solo añade al camino si la ruta tuvo éxito
                return true;
            }
        }
        
        // Si ninguna dirección funcionó, simplemente retorna false sin retroceder.
        return false;
    }

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

    private boolean findPathStepByStep(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path, Consumer<Cell> stepCallback) throws InterruptedException {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        visited[row][col] = true;
        
        stepCallback.accept(current);
        Thread.sleep(50); // Aumentamos un poco la pausa para que se vea mejor

        if (current == end) {
            path.add(current);
            return true;
        }
        
        // --- LÓGICA CORREGIDA (SIN BACKTRACKING) ---
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