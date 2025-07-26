package solver.solverImpl;

import java.util.*;

public class MazeSolverBFS {

    public static boolean resolver(int[][] lab, int inicioX, int inicioY, int finX, int finY) {
        int filas = lab.length;
        int columnas = lab[0].length;

        boolean[][] visitado = new boolean[filas][columnas];
        int[][] anterior = new int[filas * columnas][2]; // para reconstruir el camino

        Queue<int[]> cola = new LinkedList<>();
        cola.add(new int[]{inicioX, inicioY});
        visitado[inicioX][inicioY] = true;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!cola.isEmpty()) {
            int[] actual = cola.poll();
            int x = actual[0];
            int y = actual[1];

            if (x == finX && y == finY) {
                lab[x][y] = 2;
                return true;
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (nx >= 0 && ny >= 0 && nx < filas && ny < columnas &&
                        lab[nx][ny] == 0 && !visitado[nx][ny]) {
                    cola.add(new int[]{nx, ny});
                    visitado[nx][ny] = true;
                    anterior[nx * columnas + ny] = new int[]{x, y};
                }
            }
        }

        return false; // no encontró
    }
}

