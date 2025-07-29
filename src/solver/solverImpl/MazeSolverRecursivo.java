package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack; // Necesario para la implementación iterativa de paso a paso
import java.util.function.Consumer; // Aunque ya no se usa directamente para el step, la mantenemos por consistencia con otros solvers.
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto usando un enfoque recursivo simple en 2 direcciones (derecha y abajo).
 * Esta versión no utiliza backtracking explícito, construyendo el camino solo al encontrar
 * una ruta exitosa, para replicar un comportamiento visual específico.
 */
public class MazeSolverRecursivo implements MazeSolver {

    private Stack<Cell> stack; // Pila para la versión iterativa del paso a paso
    private boolean[][] visited;
    private Cell[][] mazeGrid;
    private Cell startCell;
    private Cell endCell;
    private List<Cell> finalPath;
    private boolean finished;

    /**
     * Resuelve el laberinto de forma rápida, sin animación.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        boolean[][] localVisited = new boolean[maze.length][maze[0].length];
        if (findPath(maze, start, end, localVisited, path)) {
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Función recursiva privada que busca el camino.
     */
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

        // 1. Explorar derecha PRIMERO (solo si no estamos en el borde derecho)
        if (col + 1 < maze[0].length) {
            if (findPath(maze, maze[row][col + 1], end, visited, path)) {
                path.add(current);
                return true;
            }
        }
        // 2. Explorar abajo DESPUÉS (solo si no estamos en el borde inferior)
        if (row + 1 < maze.length) {
            if (findPath(maze, maze[row + 1][col], end, visited, path)) {
                path.add(current);
                return true;
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
        this.stack = new Stack<>();
        this.visited = new boolean[maze.length][maze[0].length];
        this.finalPath = Collections.emptyList();
        this.finished = false;

        // Reiniciar padres para todas las celdas
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                maze[r][c].setParent(null);
            }
        }

        // Empieza la búsqueda desde la celda de inicio
        stack.push(startCell);
        visited[startCell.getRow()][startCell.getCol()] = true;
        // La celda de inicio no tiene padre
        startCell.setParent(null);
    }

    /**
     * Ejecuta un solo paso del algoritmo (simulando una llamada recursiva).
     * @return La celda visitada en este paso, o null si la búsqueda ha terminado o no hay más pasos.
     */
    @Override
    public Cell doStep() {
        if (finished || stack.isEmpty()) {
            finished = true; // Marca como terminado si ya lo estaba o la pila está vacía
            return null; // La búsqueda ha terminado o no hay más pasos.
        }

        Cell current = stack.pop();

        if (current.equals(endCell)) {
            finalPath = reconstructPath(current);
            finished = true;
            return current; // Se encontró el final.
        }
        
        // Explora vecinos: Derecha, luego Abajo (para DFS iterativo, se añaden en orden inverso de prioridad)
        // Por lo tanto, si la prioridad es Derecha > Abajo, añadimos Abajo a la pila y luego Derecha.
        
        // 2. Explorar Abajo
        int nextRowDown = current.getRow() + 1;
        int nextColDown = current.getCol();
        if (nextRowDown < mazeGrid.length) {
            Cell neighborDown = mazeGrid[nextRowDown][nextColDown];
            if (!visited[nextRowDown][nextColDown] && neighborDown.getState() != CellState.WALL) {
                visited[nextRowDown][nextColDown] = true;
                neighborDown.setParent(current);
                stack.push(neighborDown);
            }
        }

        // 1. Explorar Derecha
        int nextRowRight = current.getRow();
        int nextColRight = current.getCol() + 1;
        if (nextColRight < mazeGrid[0].length) {
            Cell neighborRight = mazeGrid[nextRowRight][nextColRight];
            if (!visited[nextRowRight][nextColRight] && neighborRight.getState() != CellState.WALL) {
                visited[nextRowRight][nextColRight] = true;
                neighborRight.setParent(current);
                stack.push(neighborRight);
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

    // ELIMINADO: El método solveStepByStep con Consumer ya no forma parte de la interfaz MazeSolver.
    // La implementación manual del paso a paso se hace con initializeStepByStep, doStep, etc.
}