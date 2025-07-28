package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;

public class MazeSolverBFS {

    public static List<Cell> solve(Cell[][] mazeGrid, Cell start, Cell end) {
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[mazeGrid.length][mazeGrid[0].length];

        start.setParent(null);
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.getRow() == end.getRow() && current.getCol() == end.getCol()) {
                return reconstructPath(current);
            }

            int[] dr = {-1, 1, 0, 0};
            int[] dc = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++) {
                int nRow = current.getRow() + dr[i];
                int nCol = current.getCol() + dc[i];

                if (nRow >= 0 && nCol >= 0 && nRow < mazeGrid.length && nCol < mazeGrid[0].length &&
                        !visited[nRow][nCol] && mazeGrid[nRow][nCol].getState() != CellState.WALL) {
                    
                    visited[nRow][nCol] = true;
                    Cell neighbor = mazeGrid[nRow][nCol];
                    neighbor.setParent(current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<Cell> reconstructPath(Cell endCell) {
        List<Cell> path = new ArrayList<>();
        Cell current = endCell;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}

