package solver.solverImpl;

import java.util.*;
import java.util.function.Consumer;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando el algoritmo de Búsqueda en Amplitud (BFS).
 * Este enfoque explora el laberinto por niveles, garantizando encontrar el camino
 * más corto en términos de número de celdas.
 */
public class MazeSolverBFS implements MazeSolver {

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     * @param mazeGrid La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de celdas que forman el camino más corto.
     */
    @Override
    public List<Cell> solve(Cell[][] mazeGrid, Cell start, Cell end) {
        Queue<Cell> queue = new LinkedList<>(); // Se usa una Cola (Queue) para la gestión de celdas a visitar.
        boolean[][] visited = new boolean[mazeGrid.length][mazeGrid[0].length];

        start.setParent(null); // La celda inicial no tiene padre.
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        while (!queue.isEmpty()) {
            Cell current = queue.poll(); // Se extrae la siguiente celda de la cola.

            if (current.equals(end)) {
                return reconstructPath(current); // Si es el final, se reconstruye y devuelve el camino.
            }
            
            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Se define el orden de exploración para coincidir con el del profesor.
            // Prioridad: Abajo, Arriba, Derecha, Izquierda.
            int[] dr = {1, -1, 0, 0};
            int[] dc = {0, 0, 1, -1};

            for (int i = 0; i < 4; i++) {
                int nRow = current.getRow() + dr[i];
                int nCol = current.getCol() + dc[i];

                // Se comprueba si el vecino es una celda válida.
                if (nRow >= 0 && nCol >= 0 && nRow < mazeGrid.length && nCol < mazeGrid[0].length &&
                        !visited[nRow][nCol] && mazeGrid[nRow][nCol].getState() != CellState.WALL) {
                    
                    visited[nRow][nCol] = true;
                    Cell neighbor = mazeGrid[nRow][nCol];
                    neighbor.setParent(current); // Se establece el padre para poder reconstruir el camino.
                    queue.add(neighbor); // Se añade el vecino a la cola para visitarlo después.
                }
            }
        }
        return Collections.emptyList(); // Si la cola se vacía, no hay solución.
    }

    /**
     * Reconstruye el camino desde la celda final hasta la inicial, siguiendo los "padres".
     * @param endCell La celda final encontrada por el algoritmo.
     * @return La lista de celdas que componen el camino, en orden de inicio a fin.
     */
    private List<Cell> reconstructPath(Cell endCell) {
        List<Cell> path = new ArrayList<>();
        Cell current = endCell;
        while (current != null) {
            path.add(current);
            current = current.getParent(); // Se mueve hacia atrás, al padre de la celda actual.
        }
        Collections.reverse(path); // Se invierte la lista para tener el camino en el orden correcto.
        return path;
    }

    /**
     * Resuelve el laberinto mostrando una animación paso a paso.
     */
    @Override
    public List<Cell> solveStepByStep(Cell[][] mazeGrid, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException {
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[mazeGrid.length][mazeGrid[0].length];
        start.setParent(null);
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            stepCallback.accept(current); // Publica la celda actual para que la UI la pinte.
            Thread.sleep(25);             // Pausa para que la animación sea visible.
            
            if (current.equals(end)) {
                return reconstructPath(current);
            }
            
            // Se mantiene el mismo orden de exploración que en la versión no animada.
            int[] dr = {1, -1, 0, 0};
            int[] dc = {0, 0, 1, -1};

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
}