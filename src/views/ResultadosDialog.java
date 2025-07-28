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

public class ResultadosDialog extends JDialog {

    private final JTable resultsTable;
    private final DefaultTableModel tableModel;
    public final JButton clearButton;
    public final JButton graphButton;
    private List<AlgorithmResult> currentResults;

    public ResultadosDialog(Frame owner) {
        super(owner, "Resultados Guardados", true);

        String[] columnNames = {"Algoritmo", "Tiempo (ns)", "Celdas Camino"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButton = new JButton("Limpiar Resultados");
        graphButton = new JButton("Graficar Resultados");
        buttonPanel.add(clearButton);
        buttonPanel.add(graphButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        graphButton.addActionListener(e -> displayChart());

        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    public void setResults(List<AlgorithmResult> results) {
        this.currentResults = results;
        tableModel.setRowCount(0);
        if (results == null) return;

        for (AlgorithmResult result : results) {
            Object[] row = new Object[]{
                result.getAlgorithmName(),
                result.getExecutionTime(),
                result.getPathLength()
            };
            tableModel.addRow(row);
        }
    }

// Reemplaza este método en src/views/ResultadosDialog.java

private void displayChart() {
    if (currentResults == null || currentResults.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay resultados para graficar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (AlgorithmResult result : currentResults) {
        dataset.addValue(result.getExecutionTime(), "Tiempo(ns)", result.getAlgorithmName());
    }

    JFreeChart lineChart = ChartFactory.createLineChart(
        "Tiempos de Ejecución por Algoritmo",
        "Algoritmo",
        "Tiempo (ns)",
        dataset,
        PlotOrientation.VERTICAL,
        true, true, false);

    lineChart.setBackgroundPaint(Color.WHITE);
    CategoryPlot plot = lineChart.getCategoryPlot();
    plot.setBackgroundPaint(new Color(230, 230, 230));
    plot.setDomainGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.WHITE);

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN FINAL DEL EJE Y! ---
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0"));
    
    // 1. Forzamos el rango del eje para que vaya de 0 a 200,000
    rangeAxis.setRange(0, 200000);
    
    // 2. Forzamos que los incrementos sean de 20,000 en 20,000
    rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(20000));

    // --- Mostrar el gráfico en una nueva ventana ---
    ChartPanel chartPanel = new ChartPanel(lineChart);
    JDialog chartDialog = new JDialog(this, "Gráfica", false);
    chartDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    chartDialog.setContentPane(chartPanel);
    chartDialog.pack();
    chartDialog.setLocationRelativeTo(this);
    chartDialog.setVisible(true);
}
}