
package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Cell;
import models.CellState;

public class MazeSolverDFS { // O MazeSolverRecursivoCompletoBT
    
    public static List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        findPath(maze, start, end, visited, path);
        
        // Si el camino no contiene el final, significa que no se encontró una solución.
        if (path.isEmpty() || path.get(path.size() - 1) != end) {
            return Collections.emptyList();
        }
        
        return path;
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
            return true; // ¡Éxito! Camino encontrado.
        }
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dr[i];
            int nCol = col + dc[i];

            // --- ¡ESTA ES LA CORRECCIÓN CLAVE! ---
            // Verificamos que el vecino esté DENTRO de los límites del laberinto.
            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                Cell neighbor = maze[nRow][nCol];
                if (findPath(maze, neighbor, end, visited, path)) {
                    return true; // Si se encontró el camino, dejar de buscar.
                }
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}