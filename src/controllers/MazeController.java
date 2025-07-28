package controllers;

import models.*;
import solver.solverImpl.*; // Importa todos tus solvers
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Collections;
import java.util.function.Consumer;

public class MazeController {
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
        // --- Listeners para los modos de edición ---
        mazeFrame.getSetStartButton().addActionListener(e -> setCurrentEditMode("Set Start"));
        mazeFrame.getSetEndButton().addActionListener(e -> setCurrentEditMode("Set End"));
        mazeFrame.getToggleWallButton().addActionListener(e -> setCurrentEditMode("Toggle Wall"));

        // --- Listener para el clic en el panel del laberinto ---
        mazePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() * mazeGrid[0].length / mazePanel.getWidth();
                int row = e.getY() * mazeGrid.length / mazePanel.getHeight();
                handlePanelClick(row, col);
            }
        });
        
        // --- Listener para el botón de resolver ---
        mazeFrame.getSolveButton().addActionListener(e -> {
            String selectedAlgorithm = (String) mazeFrame.getAlgorithmComboBox().getSelectedItem();
            runSolver(selectedAlgorithm);
        });

        // --- Listener para el botón de limpiar muros ---
        mazeFrame.getClearButton().addActionListener(e -> clearWalls());

        // --- Listener para la opción de menú "Ver Resultados" ---
        mazeFrame.getVerResultadosMenuItem().addActionListener(e -> mostrarResultados());

        // --- Listener para el botón "Paso a paso" (AHORA FUNCIONAL) ---
        mazeFrame.getStepButton().addActionListener(e -> {
            String selectedAlgorithm = (String) mazeFrame.getAlgorithmComboBox().getSelectedItem();
            runStepByStepSolver(selectedAlgorithm);
        });
    }

    private void setCurrentEditMode(String mode) {
        this.currentEditMode = mode;
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
                if (cell.getState() == CellState.WALL) {
                    cell.setState(CellState.PATH);
                } else if (cell.getState() == CellState.PATH) {
                    cell.setState(CellState.WALL);
                }
                break;
        }
        mazePanel.repaint();
    }

    private void runSolver(String algorithmName) {
        Cell start = findCell(CellState.START);
        Cell end = findCell(CellState.END);

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(mazeFrame, "Debe definir un punto de INICIO y FIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clearVisualPath();
        setButtonsEnabled(false);

        SwingWorker<List<Cell>, Void> worker = new SwingWorker<>() {
            long startTime;
            @Override
            protected List<Cell> doInBackground() {
                startTime = System.nanoTime();
                switch (algorithmName) {
                    case "BFS":
                        return new MazeSolverBFS().solve(mazeGrid, start, end);
                    case "DFS":
                    case "Recursivo BT":
                        return new MazeSolverRecursivoCompletoBT().solve(mazeGrid, start, end);
                    case "Recursivo (2-dir)":
                        return new MazeSolverRecursivo().solve(mazeGrid, start, end);
                    case "Recursivo (4-dir)":
                        return new MazeSolverRecursivoCompleto().solve(mazeGrid, start, end);
                    default:
                        return Collections.emptyList();
                }
            }

            @Override
            protected void done() {
                try {
                    List<Cell> path = get();
                    long duration = System.nanoTime() - startTime;
                    addAlgoritmoResult(algorithmName, path, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    // --- NUEVO: MÉTODO PARA LA VISUALIZACIÓN PASO A PASO ---
    private void runStepByStepSolver(String algorithmName) {
        Cell start = findCell(CellState.START);
        Cell end = findCell(CellState.END);

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(mazeFrame, "Debe definir un punto de INICIO y FIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        clearVisualPath();
        setButtonsEnabled(false);

        SwingWorker<List<Cell>, Cell> worker = new SwingWorker<>() {
            long startTime;
            @Override
            protected List<Cell> doInBackground() throws Exception {
                startTime = System.nanoTime();
                Consumer<Cell> stepPublisher = this::publish;

                if ("BFS".equals(algorithmName)) {
                    // Asegúrate de tener el método solveStepByStep en tu clase MazeSolverBFS
                    return new MazeSolverBFS().solveStepByStep(mazeGrid, start, end, stepPublisher);
                } else {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(mazeFrame, "Visualización 'Paso a paso' solo implementada para BFS.", "Info", JOptionPane.INFORMATION_MESSAGE)
                    );
                    // Como fallback, ejecutamos la versión normal del algoritmo seleccionado
                    return runSolverInBackground(algorithmName, start, end);
                }
            }

            @Override
            protected void process(List<Cell> chunks) {
                // Dibuja las celdas visitadas durante la animación
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
                    List<Cell> path = get();
                    long duration = System.nanoTime() - startTime;
                    addAlgoritmoResult(algorithmName, path, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    // Método auxiliar para no duplicar el switch
    private List<Cell> runSolverInBackground(String algorithmName, Cell start, Cell end) {
        switch (algorithmName) {
            case "BFS": return new MazeSolverBFS().solve(mazeGrid, start, end);
            case "DFS": case "Recursivo BT": return new MazeSolverRecursivoCompletoBT().solve(mazeGrid, start, end);
            case "Recursivo (2-dir)": return new MazeSolverRecursivo().solve(mazeGrid, start, end);
            case "Recursivo (4-dir)": return new MazeSolverRecursivoCompleto().solve(mazeGrid, start, end);
            default: return Collections.emptyList();
        }
    }
    
    private void addAlgoritmoResult(String algorithmName, List<Cell> path, long executionTime) {
        solveResults.getResults().removeIf(res -> res.getAlgorithmName().equals(algorithmName));
        AlgorithmResult result = new AlgorithmResult(algorithmName, executionTime, path);
        solveResults.addResult(result);

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
                CellState state = cell.getState();
                if (state == CellState.WALL || state == CellState.SOLUTION || state == CellState.VISITED) {
                    cell.setState(CellState.PATH);
                }
            }
        }
        mazePanel.repaint();
    }
    
    private void clearVisualPath() {
        if (mazeGrid == null) return;
        for (Cell[] row : mazeGrid) {
            for (Cell cell : row) {
                if (cell.getState() == CellState.SOLUTION || cell.getState() == CellState.VISITED) {
                    cell.setState(CellState.PATH);
                }
            }
        }
        mazePanel.repaint();
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
    
    private void setButtonsEnabled(boolean enabled) {
        mazeFrame.getSolveButton().setEnabled(enabled);
        mazeFrame.getStepButton().setEnabled(enabled);
        mazeFrame.getClearButton().setEnabled(enabled);
    }
}