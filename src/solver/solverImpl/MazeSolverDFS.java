package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando el algoritmo de Búsqueda en Profundidad (DFS).
 * Esta implementación sigue una lógica recursiva que construye el camino hacia atrás
 * una vez que se encuentra el final, replicando el comportamiento del código de referencia.
 */
public class MazeSolverDFS implements MazeSolver {

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     * @param maze La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de celdas que forman el camino. Retorna una lista vacía si no hay solución.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        // Se inicia la búsqueda recursiva.
        findPath(maze, start, end, visited, path);
        
        // Como el camino se construye en orden inverso, se invierte la lista.
        Collections.reverse(path);
        return path;
    }

    /**
     * Función recursiva privada que implementa la lógica de DFS.
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

        // --- Condición de Éxito (Caso Base) ---
        if (current.equals(end)) {
            path.add(current); // Añade la celda final para iniciar la reconstrucción del camino.
            return true;
        }
        
        // --- Paso Recursivo ---
        // Se define el orden de exploración para coincidir con el del profesor: Abajo, Arriba, Derecha, Izquierda.
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dr[i];
            int nCol = col + dc[i];

            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                Cell neighbor = maze[nRow][nCol];
                // Si la llamada recursiva a un vecino tiene éxito...
                if (findPath(maze, neighbor, end, visited, path)) {
                    path.add(current); // ...se añade la celda ACTUAL al camino en la "vuelta".
                    return true;
                }
            }
        }
        
        // Si ninguno de los vecinos encontró un camino, esta ruta no es válida.
        return false;
    }

    /**
     * Resuelve el laberinto mostrando una animación paso a paso.
     */
    @Override
    public List<Cell> solveStepByStep(Cell[][] maze, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException {
        List<Cell> path = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        findPathStepByStep(maze, start, end, visited, path, stepCallback);
        
        Collections.reverse(path);
        return path;
    }

    /**
     * Versión animada de la función recursiva de DFS.
     */
    private boolean findPathStepByStep(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path, Consumer<Cell> stepCallback) throws InterruptedException {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
    
        visited[row][col] = true;
        stepCallback.accept(current); // Publica la celda actual para que la UI la pinte.
        Thread.sleep(25);             // Pequeña pausa para que la animación sea visible.

        if (current.equals(end)) {
            path.add(current);
            return true;
        }
        
        // Se mantiene el mismo orden de exploración que en la versión no animada.
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int nRow = row + dr[i];
            int nCol = col + dc[i];
            if (nRow >= 0 && nCol >= 0 && nRow < maze.length && nCol < maze[0].length) {
                Cell neighbor = maze[nRow][nCol];
                if (findPathStepByStep(maze, neighbor, end, visited, path, stepCallback)) {
                    path.add(current);
                    return true;
                }
            }
        }
        
        return false;
    }
}