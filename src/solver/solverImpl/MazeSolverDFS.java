package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto usando Búsqueda en Profundidad (DFS) con backtracking.
 */
public class MazeSolverDFS implements MazeSolver {

    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        findPath(maze, start, end, visited, path);
        
        if (path.isEmpty() || !path.get(path.size() - 1).equals(end)) {
            return Collections.emptyList();
        }
        return path;
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

        if (current.equals(end)) return true;
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dr[i];
            int nCol = col + dc[i];
            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                if (findPath(maze, maze[nRow][nCol], end, visited, path)) return true;
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }

    @Override
    public List<Cell> solveStepByStep(Cell[][] maze, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        findPathStepByStep(maze, start, end, visited, path, stepCallback);
        
        if (path.isEmpty() || !path.get(path.size() - 1).equals(end)) {
            return Collections.emptyList();
        }
        return path;
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
        
        stepCallback.accept(current);
        Thread.sleep(25);

        if (current.equals(end)) return true;
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dr[i];
            int nCol = col + dc[i];
            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                if (findPathStepByStep(maze, maze[nRow][nCol], end, visited, path, stepCallback)) return true;
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}