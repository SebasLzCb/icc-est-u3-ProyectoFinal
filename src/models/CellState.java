package models;

/**
 * Enum que representa los posibles estados de una celda en el laberinto.
 * Facilita la legibilidad y el manejo de los tipos de celda.
 */
public enum CellState {
    PATH,       // Camino transitable
    WALL,       // Muro no transitable
    START,      // Punto de inicio
    END,        // Punto de destino
    VISITED,    // Celda visitada durante la búsqueda
    SOLUTION    // Celda que forma parte del camino solución
}