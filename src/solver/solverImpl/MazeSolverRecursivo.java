package solver.solverImpl;


public class MazeSolverRecursivo {
   public static boolean buscar2Direcciones(int[][] lab, int x, int y, int finX, int finY) {
    
    if (x < 0 || y < 0 || x >= lab.length || y >= lab[0].length || lab[x][y] != 0) {
        return false;
    }

    if (x == finX && y == finY) {
        lab[x][y] = 2; 
        return true;
    }
    lab[x][y] = 2; 

    if (buscar2Direcciones(lab, x, y + 1, finX, finY)) {
        return true;
    }

    if (buscar2Direcciones(lab, x + 1, y, finX, finY)) {
        return true;
    }
    lab[x][y] = 0;
    return false;
}


    
}
