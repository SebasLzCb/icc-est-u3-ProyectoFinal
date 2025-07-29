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

    private Queue<Cell> queue;
    private boolean[][] visited;
    private Cell[][] mazeGrid;
    private Cell startCell;
    private Cell endCell;
    private List<Cell> finalPath; // Para almacenar el camino una vez encontrado.
    private boolean finished; // Indica si la búsqueda ha terminado.

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     * @param mazeGrid La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de celdas que forman el camino más corto.
     */
    @Override
    public List<Cell> solve(Cell[][] mazeGrid, Cell start, Cell end) {
        Queue<Cell> localQueue = new LinkedList<>();
        boolean[][] localVisited = new boolean[mazeGrid.length][mazeGrid[0].length];

        start.setParent(null);
        localQueue.add(start);
        localVisited[start.getRow()][start.getCol()] = true;

        while (!localQueue.isEmpty()) {
            Cell current = localQueue.poll();

            if (current.equals(end)) {
                return reconstructPath(current);
            }
            
            int[] dr = {1, -1, 0, 0}; // Abajo, Arriba, Derecha, Izquierda
            int[] dc = {0, 0, 1, -1};

            for (int i = 0; i < 4; i++) {
                int nRow = current.getRow() + dr[i];
                int nCol = current.getCol() + dc[i];

                if (nRow >= 0 && nCol >= 0 && nRow < mazeGrid.length && nCol < mazeGrid[0].length &&
                        !localVisited[nRow][nCol] && mazeGrid[nRow][nCol].getState() != CellState.WALL) {
                    
                    localVisited[nRow][nCol] = true;
                    Cell neighbor = mazeGrid[nRow][nCol];
                    neighbor.setParent(current);
                    localQueue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Inicializa el algoritmo para una ejecución paso a paso (manual).
     */
    @Override
    public void initializeStepByStep(Cell[][] maze, Cell start, Cell end) {
        this.mazeGrid = maze;
        this.startCell = start;
        this.endCell = end;
        this.queue = new LinkedList<>();
        this.visited = new boolean[maze.length][maze[0].length];
        this.finalPath = Collections.emptyList();
        this.finished = false;

        // Reset parents for all cells to ensure a clean slate for new searches.
        // This is important because Cell objects are reused in the mazeGrid.
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                maze[r][c].setParent(null);
            }
        }

        startCell.setParent(null); // La celda inicial no tiene padre.
        queue.add(startCell);
        visited[startCell.getRow()][startCell.getCol()] = true;
    }

    /**
     * Ejecuta un solo paso del algoritmo BFS.
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    @Override
    public Cell doStep() {
        if (finished || queue.isEmpty()) {
            finished = true; // Marca como terminado si ya lo estaba o la cola está vacía
            return null; // La búsqueda ha terminado o no hay más pasos.
        }

        Cell current = queue.poll();

        if (current.equals(endCell)) {
            finalPath = reconstructPath(current);
            finished = true;
            return current; // Se encontró el final.
        }
        
        // Explora vecinos y los añade a la cola.
        int[] dr = {1, -1, 0, 0}; // Abajo, Arriba, Derecha, Izquierda
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
        return current; // Retorna la celda actual que acaba de ser procesada.
    }

    /**
     * Verifica si la búsqueda paso a paso ha terminado.
     */
    @Override
    public boolean isStepByStepFinished() {
        // La búsqueda termina si se encuentra el camino o si la cola está vacía.
        return finished;
    }

    /**
     * Devuelve el camino final después de la búsqueda paso a paso.
     */
    @Override
    public List<Cell> getFinalPath() {
        return finalPath;
    }

    /**
     * Reconstruye el camino desde la celda final hasta la inicial, siguiendo los "padres".
     * @param endCell La celda final encontrada por el algoritmo.
     * @return La lista de celdas que componen el camino, en orden de inicio a fin.
     */
    private List<Cell> reconstructPath(Cell cell) {
        List<Cell> path = new ArrayList<>();
        Cell current = cell;
        while (current != null) {
            path.add(current);
            current = current.getParent(); // Se mueve hacia atrás, al padre de la celda actual.
        }
        Collections.reverse(path); // Se invierte la lista para tener el camino en el orden correcto.
        return path;
    }

    // ELIMINADO: El método solveStepByStep con Consumer ya no forma parte de la interfaz MazeSolver.
    // @Override
    // public List<Cell> solveStepByStep(Cell[][] maze, Cell start, Cell end, Consumer<Cell> stepCallback) throws InterruptedException {
    //     // Esta implementación ya no es necesaria y causaba el error de compilación.
    //     return Collections.emptyList(); 
    // }
}