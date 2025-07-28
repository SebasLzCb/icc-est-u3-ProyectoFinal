package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.AlgorithmResult;

public class ResultadosDialog extends JDialog {

    private final JTable resultsTable;
    private final DefaultTableModel tableModel;
    // Hacemos públicos los botones para que el controlador pueda añadirles listeners
    public final JButton clearButton;
    public final JButton graphButton;

    public ResultadosDialog(Frame owner) {
        super(owner, "Resultados Guardados", true);

        // --- Configuración de la tabla ---
        String[] columnNames = {"Algoritmo", "Tiempo de Ejecución (ms)", "Celdas Camino"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // --- NUEVO: Panel de Botones (Sur) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButton = new JButton("Limpiar Resultados");
        graphButton = new JButton("Graficar Resultados");
        buttonPanel.add(clearButton);
        buttonPanel.add(graphButton);

        // --- Configurar el Layout del Diálogo ---
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); // Añadimos el panel de botones

        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    public void setResults(List<AlgorithmResult> results) {
        tableModel.setRowCount(0); // Limpiar tabla anterior
        if (results == null) return;

        for (AlgorithmResult result : results) {
            Object[] row = new Object[]{
                result.getAlgorithmName(),
                result.getExecutionTime() / 1_000_000.0, // ns a ms
                result.getPathLength()
            };
            tableModel.addRow(row);
        }
    }
}