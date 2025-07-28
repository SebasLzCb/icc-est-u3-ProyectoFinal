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

// Reemplaza el constructor en src/views/ResultadosDialog.java
public ResultadosDialog(Frame owner) {
    super(owner, "Resultados Guardados", true);

    // --- Configuración de la tabla (esto se mantiene igual) ---
    String[] columnNames = {"Algoritmo", "Tiempo de Ejecución (ms)", "Celdas Camino"};
    tableModel = new DefaultTableModel(columnNames, 0);
    resultsTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(resultsTable);

    // --- NUEVO: Panel de Botones (Sur) ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton clearButton = new JButton("Limpiar Resultados");
    JButton graphButton = new JButton("Graficar Resultados");
    buttonPanel.add(clearButton);
    buttonPanel.add(graphButton);

    // --- Configurar el Layout del Diálogo ---
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH); // Añadimos el panel de botones

    // --- ACCIONES DE LOS BOTONES ---
    clearButton.addActionListener(e -> {
        int response = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres borrar todos los resultados guardados?",
                "Confirmar Limpieza", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Lógica para limpiar la tabla y los datos. El controlador se encargará de esto.
            // Por ahora, simplemente vaciamos la tabla visible.
            tableModel.setRowCount(0);
            // Aquí se debería llamar al controlador para que borre los datos del modelo.
        }
    });

    graphButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(this, "La función de graficar aún no está implementada.", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    });

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
