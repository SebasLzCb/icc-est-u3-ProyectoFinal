// src/views/ResultadosDialog.java

package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.AlgorithmResult;

public class ResultadosDialog extends JDialog {

    private final JTable resultsTable;
    private final DefaultTableModel tableModel;

    public ResultadosDialog(Frame owner) {
        super(owner, "Resultados de los Algoritmos", true); // `true` para que sea modal
        
        // 1. Configurar el modelo de la tabla
        String[] columnNames = {"Algoritmo", "Tiempo de Ejecución (ms)", "Longitud del Camino"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);

        // 2. Añadir la tabla a un panel con scroll
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        
        // 3. (Opcional) Panel para el gráfico
        // Aquí iría el ChartPanel de JFreeChart si se implementa
        JPanel chartPanel = new JPanel();
        chartPanel.add(new JLabel("El gráfico de JFreeChart iría aquí."));

        // 4. Configurar el layout del diálogo
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(chartPanel, BorderLayout.SOUTH);
        
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    /**
     * Llena la tabla con la lista de resultados.
     * Este método será llamado por el controlador.
     * @param results La lista de resultados a mostrar.
     */
    public void setResults(List<AlgorithmResult> results) {
        // Limpiar tabla anterior
        tableModel.setRowCount(0);
        
        if (results == null) return;

        // Llenar con nuevos datos
        for (AlgorithmResult result : results) {
            Object[] row = new Object[]{
                result.getAlgorithmName(),
                result.getExecutionTime() / 1_000_000.0, // Convertir nanosegundos a milisegundos
                result.getPathLength()
            };
            tableModel.addRow(row);
        }
    }
}
