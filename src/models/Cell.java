package models;

/**
 * Representa una celda individual en el laberinto.
 * Contiene su posición (fila, columna), su estado y una referencia a su
 * "padre" para reconstruir el camino de la solución.
 */
public class Cell {
    private int row;
    private int col;
    private CellState state;
    private Cell parent; // Usado para reconstruir el camino en algoritmos como BFS

    public Cell(int row, int col, CellState state) {
        this.row = row;
        this.col = col;
        this.state = state;
        this.parent = null; // Se inicializa sin padre
    }

    // Getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getState() {
        return state;
    }

    public Cell getParent() {
        return parent;
    }

    // Setters
    public void setState(CellState state) {
        this.state = state;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Cell(" + row + ", " + col + ")";
    }
}