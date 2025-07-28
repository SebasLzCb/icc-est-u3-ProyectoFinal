package solver.solverImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Cell;
import models.CellState;
import solver.MazeSolver;

/**
 * Resuelve el laberinto utilizando un enfoque recursivo con Backtracking.
 * Explora un camino y, si llega a un punto muerto, "retrocede" (backtracks)
 * para explorar otras alternativas.
 */
public class MazeSolverRecursivoCompletoBT implements MazeSolver {

    /**
     * Método público para iniciar la resolución del laberinto.
     * @param maze La matriz 2D de celdas que representa el laberinto.
     * @param start La celda de inicio.
     * @param end La celda de destino.
     * @return Una lista de celdas que forman el camino. Retorna una lista vacía si no hay solución.
     */
    @Override
    public List<Cell> solve(Cell[][] maze, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        // Matriz para registrar las celdas visitadas en la ruta actual y evitar bucles.
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        if (findPath(maze, start, end, visited, path)) {
            return path; // Si se encontró, la lista 'path' ya tiene la ruta correcta.
        }
        
        return Collections.emptyList(); // Si no, se devuelve una lista vacía.
    }

    /**
     * Función recursiva privada que busca el camino mediante backtracking.
     * @param maze El laberinto.
     * @param current La celda que se está evaluando actualmente.
     * @param end La celda objetivo.
     * @param visited Matriz booleana para marcar las celdas visitadas.
     * @param path La lista que va construyendo el camino actual.
     * @return true si se encuentra un camino, false en caso contrario.
     */
    private boolean findPath(Cell[][] maze, Cell current, Cell end, boolean[][] visited, List<Cell> path) {
        int row = current.getRow();
        int col = current.getCol();

        // --- Casos Base (Condiciones de parada) ---
        // 1. Si la celda está fuera de los límites, es un muro o ya la visitamos, no es un camino válido.
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length ||
            maze[row][col].getState() == CellState.WALL || visited[row][col]) {
            return false;
        }
        
        // --- Tomar una decisión ---
        visited[row][col] = true; // Marcar como visitada.
        path.add(current);        // Añadir al camino actual.

        // 2. Si hemos llegado al final, ¡éxito!
        if (current == end) {
            return true;
        }
        
        // --- Explorar (Recursión) ---
        // Movimientos posibles: arriba, abajo, izquierda, derecha.
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            
            // ¡¡CORRECCIÓN IMPORTANTE!!
            // Se comprueba que las coordenadas del vecino estén DENTRO de los límites del laberinto.
            if (newRow >= 0 && newCol >= 0 && newRow < maze.length && newCol < maze[0].length) {
                Cell neighbor = maze[newRow][newCol];
                // Si la exploración del vecino tiene éxito, propagar el resultado.
                if (findPath(maze, neighbor, end, visited, path)) {
                    return true;
                }
            }
        }
        
        // --- Backtrack (Retroceder) ---
        // Si ninguna de las 4 direcciones funcionó, este no es el camino correcto.
        // Lo eliminamos de la lista 'path' y devolvemos false.
        path.remove(path.size() - 1); 
        return false;
    }
}