package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

public class MazeSolverRecursivo implements MazeSolver {

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

        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
        // 1. Intentar mover hacia la derecha (solo si no estamos en el borde derecho)
        if (col + 1 < maze[0].length) {
            if (findPath(maze, maze[row][col + 1], end, visited, path)) {
                path.add(current);
                return true;
            }
        }

        // 2. Intentar mover hacia abajo (solo si no estamos en el borde inferior)
        if (row + 1 < maze.length) {
            if (findPath(maze, maze[row + 1][col], end, visited, path)) {
                path.add(current);
                return true;
            }
        }
        return false;
    }
}