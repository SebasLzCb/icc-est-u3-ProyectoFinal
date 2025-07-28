import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import controllers.MazeController;
import models.Cell;
import models.CellState;
import solver.solverImpl.MazeSolverBFS;
import solver.solverImpl.MazeSolverDFS;
import solver.solverImpl.MazeSolverRecursivoCompletoBT;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;

public class App {

    public static void main(String[] args) {
        // Iniciar la aplicación en el hilo de despacho de eventos de Swing.
        SwingUtilities.invokeLater(() -> {
            // 1. CREACIÓN DE LA VISTA Y EL CONTROLADOR
            MazeFrame mazeFrame = new MazeFrame();
            ResultadosDialog resultadosDialog = new ResultadosDialog(mazeFrame);
            MazePanel mazePanel = mazeFrame.getMazePanel();
            MazeController controller = new MazeController(mazeFrame, mazePanel, resultadosDialog);

            // 2. CREACIÓN DEL MODELO (LABERINTO INICIAL)
            Cell[][] defaultMaze = createDefaultMaze(25, 25);
            controller.setMaze(defaultMaze);

            // 3. CONFIGURACIÓN DE LOS BOTONES Y SUS ACCIONES
            
            // --- Botón Resolver con BFS ---
            mazeFrame.getBfsButton().addActionListener(e -> {
                runSolver("BFS", controller, mazeFrame);
            });
            
            // --- Botón Resolver con DFS ---
            mazeFrame.getDfsButton().addActionListener(e -> {
                runSolver("DFS", controller, mazeFrame);
            });

            // --- Botón Resolver con Backtracking ---
            mazeFrame.getRecursiveBtButton().addActionListener(e -> {
                runSolver("Recursivo BT", controller, mazeFrame);
            });
            
            // --- Botón Ver Resultados ---
            mazeFrame.getResultsButton().addActionListener(e -> {
                controller.mostrarResultados();
            });

            // --- Botón Limpiar Solución ---
            mazeFrame.getClearButton().addActionListener(e -> {
                // Limpia la solución dibujada y los resultados almacenados.
                controller.clearResults();
            });

            // 4. MOSTRAR LA VENTANA PRINCIPAL
            // Se llama al final para asegurar que todos los componentes tengan el tamaño correcto.
            mazeFrame.pack();
            mazeFrame.setLocationRelativeTo(null);
            mazeFrame.setVisible(true);
            mazeFrame.setSize(700,700 );
        });
    }

    /**
     * Método centralizado para ejecutar cualquier algoritmo de resolución en un hilo de fondo.
     * @param algorithmName El nombre del algoritmo a ejecutar ("BFS", "DFS", "Recursivo BT").
     * @param controller El controlador de la aplicación.
     * @param mazeFrame La ventana principal para deshabilitar/habilitar botones.
     */
    private static void runSolver(String algorithmName, MazeController controller, MazeFrame mazeFrame) {
        Cell[][] maze = controller.getMaze();
        Cell start = findCell(maze, CellState.START);
        Cell end = findCell(maze, CellState.END);

        if (start == null || end == null) {
            System.err.println("Error: El punto de inicio o fin no está definido en el laberinto.");
            return;
        }

        // Deshabilitar botones para evitar ejecuciones múltiples
        setSolverButtonsEnabled(mazeFrame, false);

        // Crear y ejecutar el SwingWorker para no congelar la UI
        SwingWorker<List<Cell>, Void> worker = new SwingWorker<List<Cell>, Void>() {
            private final long startTime = System.nanoTime();

            @Override
            protected List<Cell> doInBackground() throws Exception {
                // Selecciona y ejecuta el algoritmo correcto en el hilo de fondo
                switch (algorithmName) {
                    case "BFS":
                        return MazeSolverBFS.solve(maze, start, end);
                    case "DFS":
                        return MazeSolverDFS.solve(maze, start, end);
                    case "Recursivo BT":
                        return MazeSolverRecursivoCompletoBT.solve(maze, start, end);
                    default:
                        return Collections.emptyList();
                }
            }

            @Override
            protected void done() {
                try {
                    // Obtiene el resultado (el camino) cuando el hilo de fondo termina
                    List<Cell> path = get();
                    long duration = System.nanoTime() - startTime;
                    // Llama al controlador para actualizar la vista con el resultado
                    controller.addAlgoritmoResult(algorithmName, path, duration);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    // Vuelve a habilitar los botones, sin importar si hubo un error o no
                    setSolverButtonsEnabled(mazeFrame, true);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Método de ayuda para habilitar o deshabilitar los botones de resolución.
     */
    private static void setSolverButtonsEnabled(MazeFrame frame, boolean enabled) {
        frame.getBfsButton().setEnabled(enabled);
        frame.getDfsButton().setEnabled(enabled);
        frame.getRecursiveBtButton().setEnabled(enabled);
    }

    /**
     * Método de ayuda para encontrar una celda específica (inicio o fin) en el laberinto.
     */
    private static Cell findCell(Cell[][] maze, CellState state) {
        for (Cell[] row : maze) {
            for (Cell cell : row) {
                if (cell.getState() == state) {
                    return cell;
                }
            }
        }
        return null; // No se encontró
    }

    /**
     * Crea un laberinto de ejemplo para la demostración inicial.
     */
    private static Cell[][] createDefaultMaze(int height, int width) {
        Cell[][] maze = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                maze[row][col] = new Cell(row, col, CellState.PATH);
                if (row == 0 || col == 0 || row == height - 1 || col == width - 1) {
                    maze[row][col].setState(CellState.WALL);
                }
            }
        }
        maze[1][1].setState(CellState.START);
        maze[height - 2][width - 2].setState(CellState.END);

        for (int i = 5; i < 20; i++) {
             maze[i][10].setState(CellState.WALL);
        }
        for (int i = 0; i < 15; i++) {
             maze[19][i].setState(CellState.WALL);
        }
        maze[19][10].setState(CellState.PATH); // Crear una apertura

        return maze;
    }
}
