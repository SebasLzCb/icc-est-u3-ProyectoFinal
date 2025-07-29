package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack; // Necesario para la implementación iterativa de paso a paso
import java.util.function.Consumer; // Aunque ya no se usa directamente para el step, la mantenemos por consistencia.
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando un enfoque recursivo en 4 direcciones.
 * Esta implementación es una forma de Búsqueda en Profundidad (DFS) que no utiliza
 * backtracking explícito (no elimina celdas del camino si falla).
 * El camino se construye hacia atrás una vez que se encuentra la solución.
 */
public class MazeSolverRecursivoCompleto implements MazeSolver {

    // Clase interna para manejar el estado de la recursión en la pila
    private static class RecursiveStepFrame {
        Cell current;
        int neighborIndex; // Para saber qué vecino explorar a continuación
        Cell parentCell; // Para reconstruir el camino

        RecursiveStepFrame(Cell current, Cell parentCell) {
            this.current = current;
            this.neighborIndex = 0; // Inicia con el primer vecino
            this.parentCell = parentCell;
        }
    }

    private Stack<RecursiveStepFrame> callStack; // Pila que simula las llamadas recursivas
    private boolean[][] visited;
    private Cell[][] mazeGrid;
    private Cell startCell;
    private Cell endCell;
    private List<Cell> finalPath;
    private boolean finished;

    // Orden de exploración: Abajo, Derecha, Arriba, Izquierda
    private final int[] dRow = {1, 0, -1, 0};
    private final int[] dCol = {0, 1, 0, -1};

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
        if (findPath(maze, start, end, localVisited, path)) {
            // El camino se construye desde el final hasta el inicio, por lo que es necesario invertirlo.
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada para encontrar el camino (versión sin animación).
     * @return true si se encuentra un camino desde 'current' hasta 'end', false de lo contrario.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();
        
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        visited[row][col] = true;

        if (current.equals(end)) {
            path.add(current); // Añadir la celda final al camino y propagar el éxito.
            return true;
        }
        
        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                if (findPath(maze, maze[newRow][newCol], end, visited, path)) {
                    path.add(current);
                    return true;
                }
            }
        }
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
        this.callStack = new Stack<>();
        this.visited = new boolean[maze.length][maze[0].length];
        this.finalPath = Collections.emptyList();
        this.finished = false;

        // Reset parents for all cells to ensure a clean slate for new searches.
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                maze[r][c].setParent(null);
            }
        }

        // Empieza la búsqueda desde la celda de inicio
        // La celda de inicio no tiene padre
        startCell.setParent(null);
        callStack.push(new RecursiveStepFrame(startCell, null));
        visited[startCell.getRow()][startCell.getCol()] = true;
    }

    /**
     * Ejecuta un solo paso del algoritmo (simulando una llamada recursiva).
     * Este método se encarga de simular la pila de llamadas recursivas para controlar el paso a paso.
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    @Override
    public Cell doStep() {
        if (finished || callStack.isEmpty()) {
            finished = true;
            return null; // La búsqueda ha terminado o no hay más pasos.
        }

        RecursiveStepFrame frame = callStack.peek(); // Mira el tope de la pila (simula la llamada actual)
        Cell current = frame.current;
        Cell cellToReturn = null; // La celda que se acaba de "visitar" en este paso

        // Si es la primera vez que procesamos esta celda en un "doStep", la retornamos.
        // O si ya encontramos el final, la retornamos y terminamos.
        if (frame.neighborIndex == 0) { // Indica que estamos "entrando" a esta celda por primera vez
            if (current.equals(endCell)) {
                // Si llegamos al final, reconstruimos el camino y terminamos
                finalPath = reconstructPath(current);
                finished = true;
                return current; // Retorna la celda final
            }
            cellToReturn = current; // Esta es la celda que acaba de ser "visitada"
        }

        // Continúa explorando los vecinos de la celda actual
        while (frame.neighborIndex < 4) {
            int newRow = current.getRow() + dRow[frame.neighborIndex];
            int newCol = current.getCol() + dCol[frame.neighborIndex];
            frame.neighborIndex++; // Prepara para el siguiente vecino en el próximo paso

            if (newRow >= 0 && newCol >= 0 && newRow < mazeGrid.length && newCol < mazeGrid[0].length) {
                Cell neighbor = mazeGrid[newRow][newCol];
                if (!visited[newRow][newCol] && neighbor.getState() != CellState.WALL) {
                    visited[newRow][newCol] = true;
                    neighbor.setParent(current); // Establece el padre para la reconstrucción
                    callStack.push(new RecursiveStepFrame(neighbor, current)); // "Llama" recursivamente al vecino
                    return (cellToReturn != null) ? cellToReturn : current; // Retorna la celda actual o la recién visitada si ya la tenías.
                }
            }
        }
        
        // Si todos los vecinos fueron explorados y ninguno llevó a la solución,
        // significa que esta rama no lleva al final. "Retrocede" (pop de la pila).
        callStack.pop();
        return (cellToReturn != null) ? cellToReturn : current; // Retorna la celda actual (después de explorar todos sus vecinos o antes de retroceder)
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
     * @param cell La celda final encontrada por el algoritmo.
     * @return La lista de celdas que componen el camino, en orden de inicio a fin.
     */
    private List<Cell> reconstructPath(Cell cell) {
        List<Cell> path = new ArrayList<>();
        Cell current = cell;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}