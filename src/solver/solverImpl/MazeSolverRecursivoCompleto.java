package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

public class MazeSolverRecursivoCompleto implements MazeSolver {

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
        
        // --- PROFESSOR'S EXPLORATION ORDER: Right, Down, Left, Up ---
        int[] dRow = {0, 1, 0, -1};
        int[] dCol = {1, 0, -1, 0};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                if (findPath(maze, maze[newRow][newCol], end, visited, path)) {
                    path.add(current);
                    return true;
                }
            }
        }
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
        Thread.sleep(25);
        if (current == end) {
            path.add(current);
            return true;
        }

        // --- PROFESSOR'S EXPLORATION ORDER: Right, Down, Left, Up ---
        int[] dRow = {0, 1, 0, -1};
        int[] dCol = {1, 0, -1, 0};

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
