package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando un enfoque recursivo con Backtracking.
 * Explora un camino y, si llega a un punto muerto, "retrocede" (backtracks)
 * para explorar otras alternativas.
 */
public class MazeSolverRecursivoCompletoBT implements MazeSolver {

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        if (findPath(maze, start, end, visited, path)) {
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
        path.add(current);

        if (current == end) {
            return true;
        }
        
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                Cell neighbor = maze[newRow][newCol];
                if (findPath(maze, neighbor, end, visited, path)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
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
        path.add(current);
        
        stepCallback.accept(current); // Publica la celda para la animación
        Thread.sleep(25);             // Pausa para que se vea

        if (current == end) {
            return true;
        }
        
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                Cell neighbor = maze[newRow][newCol];
                if (findPathStepByStep(maze, neighbor, end, visited, path, stepCallback)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}