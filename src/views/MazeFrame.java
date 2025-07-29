package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import controllers.App; // Asegúrate de que App esté en el paquete controllers

/**
 * Representa la ventana principal (JFrame) de la aplicación.
 * Esta clase es la parte principal de la "Vista" en el patrón MVC. Se encarga de
 * construir y mostrar todos los componentes de la interfaz de usuario.
 */
public class MazeFrame extends JFrame {

    // --- Atributos de los Componentes de la UI ---
    private final MazePanel mazePanel;
    private final JRadioButton setStartButton, setEndButton, toggleWallButton;
    private final JComboBox<String> algorithmComboBox;
    private final JButton solveButton, clearButton, stepButton;
    private final JMenuItem verResultadosMenuItem;
    private final JMenuItem nuevoLaberintoItem;

    /**
     * Constructor de la ventana principal.
     * Inicializa la ventana, establece su layout y ensambla todos los paneles y componentes.
     */
    public MazeFrame() {
        setTitle("Creador y Solucionador de Laberintos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. BARRA DE MENÚ SUPERIOR ---
        JMenuBar menuBar = new JMenuBar();
        
        // Menú "Archivo"
        JMenu archivoMenu = new JMenu("Archivo");
        verResultadosMenuItem = new JMenuItem("Ver Resultados");
        nuevoLaberintoItem = new JMenuItem("Nuevo Laberinto");
        
        nuevoLaberintoItem.addActionListener(e -> {
            this.dispose();
            App.main(new String[]{});
        });

        archivoMenu.add(nuevoLaberintoItem);
        archivoMenu.add(verResultadosMenuItem);
        menuBar.add(archivoMenu);

        // --- NUEVO: Menú "Acerca de" ---
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaDeItem = new JMenuItem("Acerca de...");
        acercaDeItem.addActionListener(e -> mostrarAcercaDe()); // Llama al nuevo método
        ayudaMenu.add(acercaDeItem);
        menuBar.add(ayudaMenu);

        setJMenuBar(menuBar);

        // --- 2. PANEL DE HERRAMIENTAS DE EDICIÓN ---
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

        // --- 3. PANEL DEL LABERINTO ---
        mazePanel = new MazePanel();
        add(new JScrollPane(mazePanel), BorderLayout.CENTER);

        // --- 4. PANEL DE ACCIONES ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
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
    
    /**
     * Muestra una ventana de diálogo con la información de los desarrolladores y el
     * enlace al repositorio del proyecto.
     */
    private void mostrarAcercaDe() {
        // Panel principal que contendrá todo
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS)); // Layout vertical

        // Título y nombres
        JLabel tituloLabel = new JLabel("Desarrollado por:");
        tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Nombres de los integrantes
        JLabel nombresLabel = new JLabel("<html>Sebastian Loza<br>Ivanna Nievecela<br>Felipe Parra<br>Wellinton </html>");
        nombresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Panel para el enlace de GitHub
        JPanel githubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        githubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel linkLabel = new JLabel("<html><a href=''>Ver Repositorio en GitHub</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Listener para abrir el enlace en el navegador
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/SebasLzCb/icc-est-u3-ProyectoFinal.git"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        githubPanel.add(new JLabel("Repositorio:"));
        githubPanel.add(linkLabel);

        // Añadir todos los componentes al panel principal
        panelPrincipal.add(tituloLabel);
        panelPrincipal.add(Box.createVerticalStrut(5)); // Espacio vertical
        panelPrincipal.add(nombresLabel);
        panelPrincipal.add(Box.createVerticalStrut(10)); // Más espacio
        panelPrincipal.add(githubPanel);

        // Mostrar el panel en un JOptionPane
        JOptionPane.showMessageDialog(this, panelPrincipal, "Acerca del Proyecto", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Getters para que el Controlador pueda acceder a los componentes ---
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