// src/solver/solverImpl/MazeSolverRecursivoCompleto.java
package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando un enfoque recursivo en 4 direcciones.
 * Es una implementación de Búsqueda en Profundidad (DFS).
 */
public class MazeSolverRecursivoCompleto implements MazeSolver {

    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];

        if (findPath(maze, start, end, visited, path)) {
            Collections.reverse(path); // Invertir el camino para que esté en orden correcto
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

        // --- Exploración Recursiva en 4 Direcciones ---
        int[] dRow = {-1, 1, 0, 0}; // Arriba, Abajo
        int[] dCol = {0, 0, -1, 1}; // Izquierda, Derecha

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            
            // Comprobamos que el vecino esté dentro de los límites
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                if (findPath(maze, maze[newRow][newCol], end, visited, path)) {
                    path.add(current);
                    return true;
                }
            }
        }
        return false;
    }
}