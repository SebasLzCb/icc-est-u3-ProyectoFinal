package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Cell;
import models.CellState;

public class MazeSolverRecursivoCompletoBT {
    public static List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        if (findPath(maze, start, end, visited, path)) {
            return path;
        }
        
        return Collections.emptyList();
    }

    private static boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
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
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            Cell neighbor = maze[row + dr[i]][col + dc[i]];
            if (findPath(maze, neighbor, end, visited, path)) {
                return true;
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}