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
 * Resuelve el laberinto utilizando un enfoque recursivo con Backtracking.
 * Explora un camino y, si llega a un punto muerto, "retrocede" (backtracks)
 * para explorar otras alternativas.
 * Esta implementación coincide con la lógica y el orden de exploración del código de referencia.
 */
public class MazeSolverRecursivoCompletoBT implements MazeSolver {

    // Clase interna para manejar el estado de la recursión en la pila para el paso a paso
    private static class RecursiveStepFrame {
        Cell current;
        int neighborIndex; // Para saber qué vecino explorar a continuación
        List<Cell> currentPathSnapshot; // Una copia del camino en este punto de la "recursión"

        RecursiveStepFrame(Cell current, List<Cell> currentPathSnapshot) {
            this.current = current;
            this.neighborIndex = 0; // Inicia con el primer vecino
            this.currentPathSnapshot = new ArrayList<>(currentPathSnapshot); // Copia el camino actual
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
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] localVisited = new boolean[maze.length][maze[0].length];
        
        if (findPath(maze, start, end, localVisited, path)) {
            return path; // El camino se construye en el orden correcto gracias al backtracking.
        }
        
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada que implementa la lógica de búsqueda con backtracking (sin animación).
     * @return true si se encuentra un camino, false en caso contrario.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        visited[row][col] = true;
        path.add(current); // Se añade la celda al camino actual.

        if (current.equals(end)) {
            return true;
        }
        
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
        path.remove(path.size() - 1); // Si ninguno de los vecinos encontró un camino, esta celda no es parte de la solución.
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
        List<Cell> initialPath = new ArrayList<>();
        // No añadir start a initialPath aquí, se añade cuando se "procesa" en doStep
        
        // Simula la primera llamada recursiva
        callStack.push(new RecursiveStepFrame(startCell, initialPath));
        // Se marca como visitado al "entrar"
        visited[startCell.getRow()][startCell.getCol()] = true;
    }

    /**
     * Ejecuta un solo paso del algoritmo (simulando una llamada recursiva con backtracking).
     * Este método se encarga de simular la pila de llamadas recursivas para controlar el paso a paso.
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    @Override
    public Cell doStep() {
        if (finished) {
            return null; // La búsqueda ya terminó.
        }

        if (callStack.isEmpty()) {
            finished = true; // La pila está vacía, no hay más caminos que explorar.
            return null;
        }

        RecursiveStepFrame frame = callStack.peek(); // Mira el tope de la pila (simula la llamada actual)
        Cell current = frame.current;
        Cell cellToReturn = null; // La celda que se acaba de "visitar" en este paso

        // Si es la primera vez que procesamos esta celda en un "doStep"
        // O si volvemos a esta celda después de explorar una rama fallida (backtracking)
        // La condición 'frame.currentPathSnapshot.isEmpty()' en la primera entrada
        // y 'frame.currentPathSnapshot.size() < (callStack.size())' para asegurar que la celda actual
        // esté en el path de su frame ANTES de explorar vecinos.
        if (!frame.currentPathSnapshot.contains(current)) {
            frame.currentPathSnapshot.add(current); // Añade la celda actual al camino de este frame.
            cellToReturn = current; // Esta es la celda que acaba de ser "visitada"
        }
        
        // Si llegamos al final, reconstruimos el camino y terminamos
        if (current.equals(endCell)) {
            finalPath = new ArrayList<>(frame.currentPathSnapshot); // El camino ya está construido en currentPathSnapshot
            finished = true;
            return current; // Retorna la celda final
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
                    callStack.push(new RecursiveStepFrame(neighbor, frame.currentPathSnapshot)); // "Llama" recursivamente al vecino con la copia del path actual
                    return (cellToReturn != null) ? cellToReturn : current; // Retorna la celda actual que se acaba de "activar"
                }
            }
        }
        
        // Si todos los vecinos fueron explorados y ninguno llevó a la solución,
        // significa que esta rama no lleva al final. "Retrocede" (pop de la pila).
        callStack.pop();
        // Si la celda actual fue añadida al path en este frame, simula el backtracking eliminándola.
        if (frame.currentPathSnapshot.contains(current)) {
            frame.currentPathSnapshot.remove(current); // Simula el backtracking visual
        }
        
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
     * Este método se usa en la versión "solve" rápida y cuando se encuentra el "endCell" en doStep.
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