package solver.solverImpl;


public class MazeSolverRecursivo {
   public static boolean buscar2Direcciones(int[][] lab, int x, int y, int finX, int finY) {
    // Si está fuera del laberinto o en una pared, parar
    if (x < 0 || y < 0 || x >= lab.length || y >= lab[0].length || lab[x][y] != 0) {
        return false;
    }
    // Si llegó al destino
    if (x == finX && y == finY) {
        lab[x][y] = 2; // Marcar como camino
        return true;
    }
    lab[x][y] = 2; 
    // Intentar mover derecha
    if (buscar2Direcciones(lab, x, y + 1, finX, finY)) {
        return true;
    }
    // Intentar mover abajo
    if (buscar2Direcciones(lab, x + 1, y, finX, finY)) {
        return true;
    }
    lab[x][y] = 0;
    return false;
}


    
}
