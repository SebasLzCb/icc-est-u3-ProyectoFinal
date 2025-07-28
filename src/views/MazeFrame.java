package views;

import javax.swing.*;
import java.awt.*;
import controllers.App; // Asegúrate de que App esté en el paquete controllers

public class MazeFrame extends JFrame {

    private final MazePanel mazePanel;
    private final JRadioButton setStartButton, setEndButton, toggleWallButton;
    private final JComboBox<String> algorithmComboBox;
    private final JButton solveButton, clearButton, stepButton;
    private final JMenuItem verResultadosMenuItem;
    private final JMenuItem nuevoLaberintoItem;

    public MazeFrame() {
        setTitle("Creador y Solucionador de Laberintos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. BARRA DE MENÚ SUPERIOR ---
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        verResultadosMenuItem = new JMenuItem("Ver Resultados");
        nuevoLaberintoItem = new JMenuItem("Nuevo Laberinto");
        
        nuevoLaberintoItem.addActionListener(e -> {
            this.dispose(); // Cierra la ventana actual
            App.main(new String[]{}); // Llama al main de App para empezar de nuevo
        });

        archivoMenu.add(nuevoLaberintoItem);
        archivoMenu.add(verResultadosMenuItem);
        menuBar.add(archivoMenu);
        setJMenuBar(menuBar);

        // --- 2. PANEL DE HERRAMIENTAS DE EDICIÓN (ARRIBA) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBorder(BorderFactory.createTitledBorder("Modo de Edición"));
        setStartButton = new JRadioButton("Poner Inicio");
        setEndButton = new JRadioButton("Poner Fin");
        toggleWallButton = new JRadioButton("Poner/Quitar Muro", true);
        ButtonGroup editGroup = new ButtonGroup();
        editGroup.add(setStartButton);
        editGroup.add(setEndButton);
        editGroup.add(toggleWallButton);
        topPanel.add(setStartButton);
        topPanel.add(setEndButton);
        topPanel.add(toggleWallButton);
        add(topPanel, BorderLayout.NORTH);

        // --- 3. PANEL DEL LABERINTO (CENTRO) ---
        mazePanel = new MazePanel();
        add(new JScrollPane(mazePanel), BorderLayout.CENTER);

        // --- 4. PANEL DE ACCIONES (ABAJO) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        // --- ¡NOMBRES CORREGIDOS Y AJUSTADOS! ---
        algorithmComboBox = new JComboBox<>(new String[]{
            "Recursivo", "Recursivo Completo", "Recursivo Completo BT", "BFS", "DFS", "Backtracking"
        });
        
        solveButton = new JButton("Resolver");
        stepButton = new JButton("Paso a paso");
        clearButton = new JButton("Limpiar Muros");

        bottomPanel.add(new JLabel("Algoritmo:"));
        bottomPanel.add(algorithmComboBox);
        bottomPanel.add(solveButton);
        bottomPanel.add(stepButton);
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    // --- Getters para que el Controlador pueda acceder a todo ---
    public MazePanel getMazePanel() { return mazePanel; }
    public JRadioButton getSetStartButton() { return setStartButton; }
    public JRadioButton getSetEndButton() { return setEndButton; }
    public JRadioButton getToggleWallButton() { return toggleWallButton; }
    public JComboBox<String> getAlgorithmComboBox() { return algorithmComboBox; }
    public JButton getSolveButton() { return solveButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getStepButton() { return stepButton; }
    public JMenuItem getVerResultadosMenuItem() { return verResultadosMenuItem; }
    public JMenuItem getNuevoLaberintoItem() { return nuevoLaberintoItem; }
}