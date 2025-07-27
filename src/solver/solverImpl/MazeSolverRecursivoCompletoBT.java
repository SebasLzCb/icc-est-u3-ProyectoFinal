package solver.solverImpl;

public class MazeSolverRecursivoCompletoBT {
    public static boolean buscarBacktracking(int[][] lab, int x, int y, int finX, int finY) {
    if (x < 0 || y < 0 || x >= lab.length || y >= lab[0].length || lab[x][y] != 0) {
        return false;
    }

    lab[x][y] = 2; // Marcar como parte del camino

    if (x == finX && y == finY) {
        return true;
    }

    if (buscarBacktracking(lab, x - 1, y, finX, finY)) return true;
    if (buscarBacktracking(lab, x + 1, y, finX, finY)) return true;
    if (buscarBacktracking(lab, x, y - 1, finX, finY)) return true;
    if (buscarBacktracking(lab, x, y + 1, finX, finY)) return true;

    lab[x][y] = 0; // Retroceder si no hay camino
    return false;
}

    
}
