package controllers;

import models.*;
import solver.MazeSolver;
import solver.solverImpl.*;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;

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
    private final SolveResults solveResults;
    private final MazeFrame mazeFrame;
    private final MazePanel mazePanel;
    private final ResultadosDialog resultadosDialog;
    private String currentEditMode = "Toggle Wall";

    public MazeController(MazeFrame mazeFrame, int rows, int cols) {
        this.mazeFrame = mazeFrame;
        this.mazePanel = mazeFrame.getMazePanel();
        this.resultadosDialog = new ResultadosDialog(mazeFrame);
        this.solveResults = new SolveResults();
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

        ActionListener solveActionListener = e -> {
            String selectedAlgorithm = (String) mazeFrame.getAlgorithmComboBox().getSelectedItem();
            runAnimatedSolver(selectedAlgorithm);
        };
        mazeFrame.getSolveButton().addActionListener(solveActionListener);
        mazeFrame.getStepButton().addActionListener(solveActionListener);

        mazeFrame.getClearButton().addActionListener(e -> clearWalls());
        mazeFrame.getVerResultadosMenuItem().addActionListener(e -> mostrarResultados());

        resultadosDialog.clearButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                resultadosDialog,
                "¿Estás seguro de que quieres borrar todos los resultados de la tabla?",
                "Confirmar Limpieza", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                solveResults.clearResults();
                resultadosDialog.setResults(solveResults.getResults());
            }
        });
    }

    /**
     * Inicia la resolución del laberinto en un hilo separado para no congelar la interfaz.
     * Mide el tiempo de ejecución real y luego muestra la animación visual.
     * @param algorithmName El nombre del algoritmo seleccionado.
     */
    private void runAnimatedSolver(String algorithmName) {
        Cell start = findCell(CellState.START);
        Cell end = findCell(CellState.END);
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(mazeFrame, "Debe definir un punto de INICIO y FIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        clearVisualPath();
        setButtonsEnabled(false);

        SwingWorker<SolveResultPayload, Cell> worker = new SwingWorker<>() {
            @Override
            protected SolveResultPayload doInBackground() throws Exception {
                Consumer<Cell> stepPublisher = this::publish;
                MazeSolver solver = getSolverByName(algorithmName);

                long startTime = System.nanoTime();
                List<Cell> path = solver.solve(mazeGrid, start, end);
                long duration = System.nanoTime() - startTime;
                
                clearVisitedState();
                
                solver.solveStepByStep(mazeGrid, start, end, stepPublisher);
                
                return new SolveResultPayload(path, duration);
            }

            @Override
            protected void process(List<Cell> chunks) {
                for (Cell cell : chunks) {
                    if (cell.getState() == CellState.PATH) {
                        cell.setState(CellState.VISITED);
                    }
                }
                mazePanel.repaint();
            }

            @Override
            protected void done() {
                try {
                    SolveResultPayload result = get();
                    clearVisualPath();
                    addAlgoritmoResult(algorithmName, result.path(), result.duration());

                    // --- ¡AQUÍ ESTÁ LA LÓGICA CORREGIDA! ---
                    // Si la lista del camino está vacía, significa que no se encontró solución.
                    if (result.path().isEmpty()) {
                        JOptionPane.showMessageDialog(mazeFrame, "No se encontró un camino al destino.", "Sin Solución", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mazeFrame, "Ocurrió un error al resolver:\n" + e.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
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
            // --- LÓGICA REVERTIDA A LA ORIGINAL ---
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
    }
    
    private void handlePanelClick(int row, int col) {
        if (row < 0 || row >= mazeGrid.length || col < 0 || col >= mazeGrid[0].length) return;
        switch (currentEditMode) {
            case "Set Start":
                findAndReplaceState(CellState.START, CellState.PATH);
                mazeGrid[row][col].setState(CellState.START);
                break;
            case "Set End":
                findAndReplaceState(CellState.END, CellState.PATH);
                mazeGrid[row][col].setState(CellState.END);
                break;
            case "Toggle Wall":
                Cell cell = mazeGrid[row][col];
                if (cell.getState() == CellState.WALL) cell.setState(CellState.PATH);
                else if (cell.getState() == CellState.PATH) cell.setState(CellState.WALL);
                break;
        }
        mazePanel.repaint();
    }
    
    private void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        solveResults.getResults().removeIf(res -> res.getAlgorithmName().equals(algorithmName));
        solveResults.addResult(new AlgorithmResult(algorithmName, executionTime, path));
        for (Cell cell : path) {
            if (cell.getState() == CellState.PATH || cell.getState() == CellState.VISITED) {
                cell.setState(CellState.SOLUTION);
            }
        }
        mazePanel.repaint();
    }
    
    private void mostrarResultados() {
        resultadosDialog.setResults(solveResults.getResults());
        resultadosDialog.setVisible(true);
    }
    
    private void clearWalls() {
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == CellState.WALL || cell.getState() == CellState.SOLUTION || cell.getState() == CellState.VISITED) {
                    cell.setState(CellState.PATH);
                }
            }
        }
        mazePanel.repaint();
    }
    
    private void clearVisualPath() {
        clearState(CellState.SOLUTION);
        clearState(CellState.VISITED);
        mazePanel.repaint();
    }

    private void clearVisitedState() {
        clearState(CellState.VISITED);
    }

    private void clearState(CellState stateToClear) {
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
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == toFind) {
                    cell.setState(toReplace);
                    return;
                }
            }
        }
    }
    
    private Cell findCell(CellState state) {
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == state) return cell;
            }
        }
        return null;
    }
    
    private Cell[][] createDefaultMaze(int height, int width) {
        Cell[][] maze = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                maze[row][col] = new Cell(row, col, CellState.PATH);
            }
        }
        return maze;
    }
}