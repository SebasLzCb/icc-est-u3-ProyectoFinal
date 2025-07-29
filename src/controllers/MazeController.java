package controllers;

import models.*;
import solver.MazeSolver;
import solver.solverImpl.*;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;
import dao.AlgorithmResultDAO; // Importar la interfaz DAO
import dao.daoImpl.AlgorithmResultDAOFile; // Importar la implementación DAO

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Collections;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * El controlador principal de la aplicación, siguiendo el patrón Modelo-Vista-Controlador (MVC).
 * Se encarga de manejar toda la lógica de la aplicación, actuar como intermediario entre
 * la vista (la interfaz gráfica) y el modelo (los datos del laberinto y los algoritmos).
 */
public class MazeController {
    // Clase interna para pasar el resultado del hilo de fondo al hilo principal de forma segura.
    private record SolveResultPayload(List<Cell> path, long duration) {}

    private Cell[][] mazeGrid;
    private final SolveResults solveResults; // Este es tu contenedor in-memory de resultados
    private final MazeFrame mazeFrame;
    private final MazePanel mazePanel;
    private final ResultadosDialog resultadosDialog;
    private String currentEditMode = "Toggle Wall";

    // --- NUEVAS VARIABLES PARA EL MODO PASO A PASO MANUAL ---
    private MazeSolver currentStepSolver;
    private boolean stepByStepActive = false;
    private Cell stepByStepStart;
    private Cell stepByStepEnd;
    private String currentStepAlgorithmName;

    // --- AÑADIR LA INSTANCIA DEL DAO ---
    private final AlgorithmResultDAO resultDAO;

    public MazeController(MazeFrame mazeFrame, int rows, int cols) {
        this.mazeFrame = mazeFrame;
        this.mazePanel = mazeFrame.getMazePanel();
        this.resultadosDialog = new ResultadosDialog(mazeFrame);
        this.solveResults = new SolveResults(); 
        // INSTANCIAR EL DAO AQUÍ
        this.resultDAO = new AlgorithmResultDAOFile(); // Esto llamará al constructor del DAO y creará el CSV
        // Cargar los resultados existentes del archivo al iniciar el controlador
        this.solveResults.getResults().addAll(resultDAO.getAllResults()); 

        this.mazeGrid = createDefaultMaze(rows, cols);
        this.mazePanel.setMazeGrid(this.mazeGrid);
        initializeListeners();
    }

    private void initializeListeners() {
        // Asigna un listener a cada botón de la interfaz para que el controlador pueda reaccionar.
        mazeFrame.getSetStartButton().addActionListener(e -> setCurrentEditMode("Set Start"));
        mazeFrame.getSetEndButton().addActionListener(e -> setCurrentEditMode("Set End"));
        mazeFrame.getToggleWallButton().addActionListener(e -> setCurrentEditMode("Toggle Wall"));

        mazePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() * mazeGrid[0].length / mazePanel.getWidth();
                int row = e.getY() * mazeGrid.length / mazePanel.getHeight();
                handlePanelClick(row, col);
            }
        });

        // Listener para el botón "Resolver" (ejecución automática)
        mazeFrame.getSolveButton().addActionListener(e -> {
            String selectedAlgorithm = (String) mazeFrame.getAlgorithmComboBox().getSelectedItem();
            runAutomaticSolver(selectedAlgorithm); // Llama al nuevo método para ejecución automática
        });

        // Listener para el botón "Paso a paso" (ejecución manual por clic)
        mazeFrame.getStepButton().addActionListener(e -> {
            handleStepByStepClick(); // Llama al nuevo método para el paso a paso manual
        });

        mazeFrame.getClearButton().addActionListener(e -> clearWalls());
        mazeFrame.getVerResultadosMenuItem().addActionListener(e -> mostrarResultados());

        resultadosDialog.clearButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                resultadosDialog,
                "¿Estás seguro de que quieres borrar todos los resultados de la tabla?",
                "Confirmar Limpieza", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                solveResults.clearResults(); // Limpia la lista en memoria
                resultDAO.clearResults();    // Limpia el archivo CSV
                resultadosDialog.setResults(solveResults.getResults()); // Actualiza la vista
            }
        });
    }

    /**
     * Inicia la resolución del laberinto en un hilo separado para no congelar la interfaz (modo automático).
     * Mide el tiempo de ejecución real y luego muestra la solución final.
     * @param algorithmName El nombre del algoritmo seleccionado.
     */
    private void runAutomaticSolver(String algorithmName) {
        Cell start = findCell(CellState.START);
        Cell end = findCell(CellState.END);
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(mazeFrame, "Debe definir un punto de INICIO y FIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        clearVisualPath();
        setButtonsEnabled(false); // Deshabilita botones durante la resolución automática

        SwingWorker<SolveResultPayload, Void> worker = new SwingWorker<>() { // Ya no publica Cells en process para este modo
            @Override
            protected SolveResultPayload doInBackground() throws Exception {
                MazeSolver solver = getSolverByName(algorithmName);

                long startTime = System.nanoTime();
                List<Cell> path = solver.solve(mazeGrid, start, end); // Resuelve sin animación
                long duration = System.nanoTime() - startTime;
                
                return new SolveResultPayload(path, duration);
            }

            @Override
            protected void done() {
                try {
                    SolveResultPayload result = get();
                    clearVisualPath(); // Asegura limpieza visual si había algo anterior
                    addAlgoritmoResult(algorithmName, result.path(), result.duration()); // Guarda en memoria y visualiza
                    resultDAO.saveResult(new AlgorithmResult(algorithmName, result.duration(), result.path())); // GUARDA EN EL ARCHIVO CSV

                    if (result.path().isEmpty()) {
                        JOptionPane.showMessageDialog(mazeFrame, "No se encontró un camino al destino.", "Sin Solución", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mazeFrame, "¡Búsqueda completada! Camino encontrado para el algoritmo " + algorithmName + ".", "Búsqueda Completada", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String errorMessage = "Ocurrió un error al resolver.";
                    if (e.getCause() != null) {
                        errorMessage += "\nCausa: " + e.getCause().getMessage();
                    } else if (e.getMessage() != null) {
                        errorMessage += "\nMensaje: " + e.getMessage();
                    }
                    JOptionPane.showMessageDialog(mazeFrame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setButtonsEnabled(true); // Habilita botones al finalizar
                }
            }
        };
        worker.execute();
    }

    /**
     * Maneja el clic en el botón "Paso a paso" (modo manual).
     * Cada clic avanza un solo paso del algoritmo.
     */
    private void handleStepByStepClick() {
        if (!stepByStepActive) {
            // Inicializar la búsqueda paso a paso si no está activa
            stepByStepStart = findCell(CellState.START);
            stepByStepEnd = findCell(CellState.END);

            if (stepByStepStart == null || stepByStepEnd == null) {
                JOptionPane.showMessageDialog(mazeFrame, "Debe definir un punto de INICIO y FIN para el modo paso a paso.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Limpiar visualmente el laberinto antes de iniciar el paso a paso
            clearVisualPath();
            
            currentStepAlgorithmName = (String) mazeFrame.getAlgorithmComboBox().getSelectedItem();
            currentStepSolver = getSolverByName(currentStepAlgorithmName);
            
            // Inicializar el solver para el modo paso a paso
            currentStepSolver.initializeStepByStep(mazeGrid, stepByStepStart, stepByStepEnd);
            stepByStepActive = true;
            
            // Deshabilitar botones de edición y resolver mientras el paso a paso está activo
            mazeFrame.getSolveButton().setEnabled(false);
            mazeFrame.getClearButton().setEnabled(false);
            mazeFrame.getSetStartButton().setEnabled(false);
            mazeFrame.getSetEndButton().setEnabled(false);
            mazeFrame.getToggleWallButton().setEnabled(false);
            // El botón de "Paso a paso" se mantiene habilitado para seguir haciendo clics.

        }

        // Ejecutar el siguiente paso
        if (currentStepSolver != null && !currentStepSolver.isStepByStepFinished()) {
            Cell stepCell = currentStepSolver.doStep();
            if (stepCell != null) {
                // Marcar la celda como visitada si no es START o END
                if (stepCell.getState() == CellState.PATH) {
                    stepCell.setState(CellState.VISITED);
                }
                mazePanel.repaint(); // Repintar para mostrar la celda actual
            }

            // Verificar si la búsqueda ha terminado después de este paso
            if (currentStepSolver.isStepByStepFinished()) {
                List<Cell> finalPath = currentStepSolver.getFinalPath();
                long duration = 0; // En modo paso a paso manual, el tiempo de cada "doStep" no se mide así.
                                   // Para fines de registro, podrías iniciar un cronómetro al comienzo del
                                   // step-by-step y detenerlo aquí. Por ahora, lo dejamos en 0.
                
                // Limpia los estados VISITADOS antes de pintar la solución final
                clearVisualPath(); 
                // Añade el resultado de la búsqueda manual (tiempo 0 si no se mide específicamente para esto)
                addAlgoritmoResult(currentStepAlgorithmName, finalPath, duration);
                // GUARDA EN EL ARCHIVO CSV
                resultDAO.saveResult(new AlgorithmResult(currentStepAlgorithmName, duration, finalPath));

                if (finalPath.isEmpty()) {
                    JOptionPane.showMessageDialog(mazeFrame, "No se encontró un camino al destino en el modo paso a paso.", "Sin Solución", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mazeFrame, "¡Búsqueda paso a paso completada! Camino encontrado para el algoritmo " + currentStepAlgorithmName + ".", "Búsqueda Completada", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Restablecer el estado para futuras búsquedas paso a paso o automáticas
                stepByStepActive = false;
                currentStepSolver = null;
                setButtonsEnabled(true); // Habilita todos los botones nuevamente
            }
        } else if (stepByStepActive && currentStepSolver.isStepByStepFinished()) {
            // Caso donde se hace clic en "Paso a paso" después de que ya terminó
            JOptionPane.showMessageDialog(mazeFrame, "La búsqueda paso a paso ya ha terminado.", "Info", JOptionPane.INFORMATION_MESSAGE);
            setButtonsEnabled(true); // Asegurar que los botones estén habilitados
            stepByStepActive = false;
            currentStepSolver = null;
        }
    }
    
    /**
     * Devuelve una instancia del algoritmo solicitado.
     * @param name El nombre del algoritmo.
     * @return una instancia de MazeSolver.
     */
    private MazeSolver getSolverByName(String name) {
        switch (name) {
            case "BFS": return new MazeSolverBFS();
            case "DFS": return new MazeSolverDFS();
            case "Backtracking":
            case "Recursivo Completo BT":
                return new MazeSolverRecursivoCompletoBT();
            case "Recursivo": 
                return new MazeSolverRecursivo();
            case "Recursivo Completo":
                return new MazeSolverRecursivoCompleto();
            default: return null;
        }
    }
    
    // --- MÉTODOS AUXILIARES ---

    private void setCurrentEditMode(String mode) { this.currentEditMode = mode; }
    
    private void setButtonsEnabled(boolean enabled) {
        mazeFrame.getSolveButton().setEnabled(enabled);
        mazeFrame.getStepButton().setEnabled(enabled);
        mazeFrame.getClearButton().setEnabled(enabled);
        // Habilita/deshabilita los botones de edición también
        mazeFrame.getSetStartButton().setEnabled(enabled);
        mazeFrame.getSetEndButton().setEnabled(enabled);
        mazeFrame.getToggleWallButton().setEnabled(enabled);
    }
    
    private void handlePanelClick(int row, int col) {
        // Si el modo paso a paso está activo, no permitimos la edición del laberinto.
        if (stepByStepActive) {
            JOptionPane.showMessageDialog(mazeFrame, "No se puede editar el laberinto mientras el modo paso a paso está activo. Finalice la búsqueda o reinicie el laberinto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica que las coordenadas estén dentro de los límites del laberinto.
        if (row < 0 || row >= mazeGrid.length || col < 0 || col >= mazeGrid[0].length) return;
        switch (currentEditMode) {
            case "Set Start":
                // Elimina cualquier otro punto de inicio y establece el nuevo.
                findAndReplaceState(CellState.START, CellState.PATH);
                mazeGrid[row][col].setState(CellState.START);
                break;
            case "Set End":
                // Elimina cualquier otro punto final y establece el nuevo.
                findAndReplaceState(CellState.END, CellState.PATH);
                mazeGrid[row][col].setState(CellState.END);
                break;
            case "Toggle Wall":
                // Cambia el estado de la celda entre WALL y PATH.
                Cell cell = mazeGrid[row][col];
                if (cell.getState() == CellState.WALL) cell.setState(CellState.PATH);
                else if (cell.getState() == CellState.PATH) cell.setState(CellState.WALL);
                break;
        }
        mazePanel.repaint(); // Repinta el panel después de un cambio en el laberinto.
    }
    
    private void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        // Elimina el resultado anterior si el algoritmo ya existía para evitar duplicados en la tabla.
        solveResults.getResults().removeIf(res -> res.getAlgorithmName().equals(algorithmName));
        // Añade el nuevo resultado al modelo en memoria.
        solveResults.addResult(new AlgorithmResult(algorithmName, executionTime, path)); 
        
        // Marca las celdas de la ruta de la solución final.
        for (Cell cell : path) {
            // Asegura no sobrescribir START o END si son parte del camino.
            if (cell.getState() == CellState.PATH || cell.getState() == CellState.VISITED) {
                cell.setState(CellState.SOLUTION);
            }
        }
        mazePanel.repaint(); // Repinta el laberinto para mostrar la solución final.
    }
    
    private void mostrarResultados() {
        // Actualiza y muestra el diálogo de resultados con los datos actuales.
        // Los datos se leen del DAO cada vez que se muestra el diálogo para asegurar que estén al día con el CSV.
        solveResults.getResults().clear(); // Limpia la memoria antes de recargar
        solveResults.getResults().addAll(resultDAO.getAllResults()); // Carga desde el archivo
        resultadosDialog.setResults(solveResults.getResults());
        resultadosDialog.setVisible(true);
    }
    
    private void clearWalls() {
        // Si el modo paso a paso está activo, no permitimos limpiar.
        if (stepByStepActive) {
            JOptionPane.showMessageDialog(mazeFrame, "No se puede limpiar el laberinto mientras el modo paso a paso está activo. Finalice la búsqueda o reinicie el laberinto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Restablece todas las celdas que son muros, solución o visitadas a PATH.
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == CellState.WALL || cell.getState() == CellState.SOLUTION || cell.getState() == CellState.VISITED) {
                    cell.setState(CellState.PATH);
                }
            }
        }
        mazePanel.repaint(); // Repinta para reflejar la limpieza.
    }
    
    private void clearVisualPath() {
        // Limpia solo los estados visuales temporales (solución pintada y visitadas).
        clearState(CellState.SOLUTION);
        clearState(CellState.VISITED);
        mazePanel.repaint(); // Asegura que los cambios se visualicen inmediatamente.
    }

    private void clearVisitedState() {
        // Específicamente para limpiar solo las celdas que fueron marcadas como VISITADAS durante la animación.
        clearState(CellState.VISITED);
    }

    private void clearState(CellState stateToClear) {
        // Recorre la cuadrícula y restablece las celdas con 'stateToClear' a PATH.
        if (mazeGrid == null) return;
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == stateToClear) {
                    cell.setState(CellState.PATH);
                }
            }
        }
    }
    
    private void findAndReplaceState(CellState toFind, CellState toReplace) {
        // Busca una celda con 'toFind' y cambia su estado a 'toReplace'. Útil para reubicar START/END.
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == toFind) {
                    cell.setState(toReplace);
                    return; // Se asume que solo hay una celda de este tipo (START o END).
                }
            }
        }
    }
    
    private Cell findCell(CellState state) {
        // Busca y retorna la primera celda que coincida con el estado dado.
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == state) return cell;
            }
        }
        return null; // Retorna null si la celda no se encuentra.
    }
    
    private Cell[][] createDefaultMaze(int height, int width) {
        // Inicializa un nuevo laberinto con todas las celdas en estado PATH.
        Cell[][] maze = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                maze[row][col] = new Cell(row, col, CellState.PATH);
            }
        }
        return maze;
    }
}