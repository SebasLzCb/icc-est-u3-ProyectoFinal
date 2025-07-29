package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import models.AlgorithmResult;

/**
 * Representa la ventana de diálogo (JDialog) que muestra los resultados de los algoritmos.
 * Esta clase es parte de la "Vista" en el patrón MVC. Se encarga de mostrar los datos
 * en una tabla y de generar un gráfico de líneas comparativo.
 */
public class ResultadosDialog extends JDialog {

    // --- Atributos de los Componentes de la UI ---
    private final JTable resultsTable;
    private final DefaultTableModel tableModel;
    public final JButton clearButton; // Público para que el Controller añada su listener
    public final JButton graphButton; // Público para que el Controller añada su listener
    private List<AlgorithmResult> currentResults; // Almacena los últimos resultados recibidos

    /**
     * Constructor del diálogo de resultados.
     * @param owner El Frame principal que es "dueño" de este diálogo.
     */
    public ResultadosDialog(Frame owner) {
        super(owner, "Resultados Guardados", true); // 'true' para que sea modal

        // --- Configuración de la Tabla ---
        String[] columnNames = {"Algoritmo", "Tiempo (ns)", "Celdas Camino"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // --- Configuración del Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButton = new JButton("Limpiar Resultados");
        graphButton = new JButton("Graficar Resultados");
        buttonPanel.add(clearButton);
        buttonPanel.add(graphButton);

        // --- Ensamblaje de la Ventana ---
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Se asigna la acción al botón de graficar
        graphButton.addActionListener(e -> displayChart());

        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    /**
     * Actualiza la tabla con una nueva lista de resultados.
     * @param results La lista de objetos AlgorithmResult a mostrar.
     */
    public void setResults(List<AlgorithmResult> results) {
        this.currentResults = results; // Guarda la lista para usarla después en el gráfico
        tableModel.setRowCount(0); // Limpia los datos anteriores de la tabla
        if (results == null) return;

        // Itera sobre la lista de resultados y añade cada uno como una nueva fila en la tabla.
        for (AlgorithmResult result : results) {
            Object[] row = new Object[]{
                result.getAlgorithmName(),
                result.getExecutionTime(), // Muestra el tiempo en nanosegundos
                result.getPathLength()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Crea y muestra el gráfico de líneas en una nueva ventana.
     * Utiliza la librería JFreeChart para la visualización.
     */
    private void displayChart() {
        if (currentResults == null || currentResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay resultados para graficar.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 1. Crear el Dataset: la estructura de datos que JFreeChart entiende.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (AlgorithmResult result : currentResults) {
            // Se añade cada resultado al dataset.
            dataset.addValue(result.getExecutionTime(), "Tiempo(ns)", result.getAlgorithmName());
        }

        // 2. Crear el Gráfico: se usa ChartFactory para generar un gráfico de líneas.
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Tiempos de Ejecución por Algoritmo",
            "Algoritmo",
            "Tiempo (ns)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false);

        // 3. Personalizar la Apariencia del Gráfico
        lineChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(230, 230, 230)); // Fondo gris claro
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.WHITE);

        // 4. Formatear el Eje Y para que tenga incrementos fijos y uniformes
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0")); // Formato sin decimales y con separador de miles
        rangeAxis.setRange(0, 200000); // Fija el rango del eje de 0 a 200,000
        rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(20000)); // Fija los incrementos a 20,000

        // 5. Mostrar el Gráfico en una Nueva Ventana
        ChartPanel chartPanel = new ChartPanel(lineChart);
        JDialog chartDialog = new JDialog(this, "Gráfica", false); // Se usa JDialog para evitar problemas de enfoque
        chartDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        chartDialog.setContentPane(chartPanel);
        chartDialog.pack(); // Ajusta el tamaño de la ventana al contenido
        chartDialog.setLocationRelativeTo(this); // Centra la ventana
        chartDialog.setVisible(true);
    }
}