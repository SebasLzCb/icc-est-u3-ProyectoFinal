package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack; // Usamos Stack para simular el comportamiento de la recursión/DFS de forma iterativa
import java.util.function.Consumer; // Aunque ya no se usa directamente para el step, la mantenemos por consistencia con otros solvers si la usaran.
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando el algoritmo de Búsqueda en Profundidad (DFS).
 * Esta implementación sigue una lógica recursiva que construye el camino hacia atrás
 * una vez que se encuentra el final, replicando el comportamiento del código de referencia.
 */
public class MazeSolverDFS implements MazeSolver {

    private Stack<Cell> stack; // Usamos un Stack para la versión iterativa de DFS para el paso a paso
    private boolean[][] visited;
    private Cell[][] mazeGrid;
    private Cell startCell;
    private Cell endCell;
    private List<Cell> finalPath;
    private boolean finished;

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
        boolean[][] localVisited = new boolean[maze.length][maze[0].length];
        
        // Se inicia la búsqueda recursiva.
        findPath(maze, start, end, localVisited, path);
        
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
     * Inicializa el algoritmo para una ejecución paso a paso (manual).
     */
    @Override
    public void initializeStepByStep(Cell[][] maze, Cell start, Cell end) {
        this.mazeGrid = maze;
        this.startCell = start;
        this.endCell = end;
        this.stack = new Stack<>(); // Usamos un Stack para la implementación iterativa de DFS.
        this.visited = new boolean[maze.length][maze[0].length];
        this.finalPath = Collections.emptyList();
        this.finished = false;

        // Reset parents for all cells to ensure a clean slate for new searches.
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                maze[r][c].setParent(null);
            }
        }

        stack.push(startCell);
        visited[startCell.getRow()][startCell.getCol()] = true;
    }

    /**
     * Ejecuta un solo paso del algoritmo DFS.
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    @Override
    public Cell doStep() {
        if (finished || stack.isEmpty()) {
            finished = true;
            return null; // La búsqueda ha terminado o no hay más pasos.
        }

        Cell current = stack.pop();

        // Marcar como visitado (si no lo estaba ya por alguna razón) y procesar visualmente
        // En DFS iterativo, la celda se marca como visitada al *añadirla* a la pila,
        // así que aquí solo procesamos la que sacamos.
        // if (current.getState() == CellState.PATH) { // No modificamos directamente el estado aquí, lo hará el controlador
        //    current.setState(CellState.VISITED);
        // }

        if (current.equals(endCell)) {
            finalPath = reconstructPath(current);
            finished = true;
            return current; // Se encontró el final.
        }
        
        // Explora vecinos en el orden DFS y los añade a la pila.
        // Se define el orden de exploración: Abajo, Arriba, Derecha, Izquierda.
        // Para DFS iterativo con Stack, los últimos en añadir son los primeros en procesar (LIFO).
        // Si queremos procesar Abajo PRIMERO, lo añadimos ÚLTIMO a la pila.
        int[] dr = {1, -1, 0, 0}; // Abajo, Arriba, Derecha, Izquierda
        int[] dc = {0, 0, 1, -1};

        // Recorrer en orden inverso para que los de mayor prioridad se queden arriba de la pila
        // Es decir, si queremos Abajo, Derecha, Arriba, Izquierda, añadimos Izquierda, Arriba, Derecha, Abajo.
        for (int i = 3; i >= 0; i--) { // Iteramos de Izquierda a Abajo
            int nRow = current.getRow() + dr[i];
            int nCol = current.getCol() + dc[i];

            if (nRow >= 0 && nCol >= 0 && nRow < mazeGrid.length && nCol < mazeGrid[0].length &&
                    !visited[nRow][nCol] && mazeGrid[nRow][nCol].getState() != CellState.WALL) {
                
                visited[nRow][nCol] = true; // Marca como visitado al añadir a la pila
                Cell neighbor = mazeGrid[nRow][nCol];
                neighbor.setParent(current); // Establece el padre para reconstruir el camino
                stack.push(neighbor); // Añade el vecino a la pila.
            }
        }
        return current; // Retorna la celda actual que acaba de ser procesada.
    }

    /**
     * Verifica si la búsqueda paso a paso ha terminado.
     */
    @Override
    public boolean isStepByStepFinished() {
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
    // La implementación manual del paso a paso se hace con initializeStepByStep y doStep.
}