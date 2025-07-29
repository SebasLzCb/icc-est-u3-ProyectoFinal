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
 * Esta implementación coincide con la lógica y el orden de exploración del código de referencia.
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
            return path; // El camino se construye en el orden correcto gracias al backtracking.
        }
        
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada que implementa la lógica de búsqueda con backtracking.
     * @return true si se encuentra un camino, false en caso contrario.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();

        // --- Condición de Parada (Caso Base) ---
        // Si la celda está fuera de los límites, es un muro o ya fue visitada, no es un camino válido.
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        visited[row][col] = true;
        path.add(current); // Se añade la celda al camino actual.

        // --- Condición de Éxito (Caso Base) ---
        if (current.equals(end)) {
            return true;
        }
        
        // --- Paso Recursivo ---
        // Se define el orden de exploración para coincidir con el del profesor:
        // Prioridad: Abajo, Derecha, Arriba, Izquierda.
        int[] dRow = {1, 0, -1, 0};
        int[] dCol = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dRow[i];
            int nCol = col + dCol[i];

            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                Cell neighbor = maze[nRow][nCol];
                if (findPath(maze, neighbor, end, visited, path)) {
                    return true; // Si un vecino encuentra el camino, se propaga el éxito.
                }
            }
        }
        
        // --- Backtracking ---
        // Si ninguno de los vecinos encontró un camino, esta celda no es parte de la solución.
        // Se elimina del camino y se retorna false para "retroceder".
        path.remove(path.size() - 1);
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

    /**
     * Versión animada de la función recursiva de búsqueda con backtracking.
     */
    private boolean findPathStepByStep(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path, Consumer<Cell> stepCallback) throws InterruptedException {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
    
        visited[row][col] = true;
        path.add(current);
        
        stepCallback.accept(current); // Publica la celda actual para que la UI la pinte.
        Thread.sleep(25);             // Pequeña pausa para que la animación sea visible.

        if (current.equals(end)) {
            return true;
        }
        
        // Se mantiene el mismo orden de exploración que en la versión no animada.
        int[] dRow = {1, 0, -1, 0};
        int[] dCol = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dRow[i];
            int nCol = col + dCol[i];
            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                Cell neighbor = maze[nRow][nCol];
                if (findPathStepByStep(maze, neighbor, end, visited, path, stepCallback)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}