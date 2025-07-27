package solver;

import java.util.List;

import models.Cell;

public interface MazeSolver {
    List<Cell> getPath(boolean[][] grid,Cell start,Cell end);

}
