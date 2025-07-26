package solver.solverImpl;


public class MazeSolverRecursivoCompleto {
    public static boolean buscar4Direcciones(int[][] lab, int x, int y, int finX, int finY) {
    if (x < 0 || y < 0 || x >= lab.length || y >= lab[0].length || lab[x][y] != 0) {
        return false;
    }

    if (x == finX && y == finY) {
        lab[x][y] = 2;
        return true;
    }

    lab[x][y] = 2;

    if (buscar4Direcciones(lab, x - 1, y, finX, finY)) return true; // arriba
    if (buscar4Direcciones(lab, x + 1, y, finX, finY)) return true; // abajo
    if (buscar4Direcciones(lab, x, y - 1, finX, finY)) return true; // izquierda
    if (buscar4Direcciones(lab, x, y + 1, finX, finY)) return true; // derecha

    lab[x][y] = 0;
    return false;
}


    
}
