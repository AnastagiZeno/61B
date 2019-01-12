/* Maze.java */

import java.util.*;

import set.*;

/**
 * The Maze class represents a maze in a rectangular grid.  There is exactly
 * one path between any two points.
 **/

public class Maze {

    // Horizontal and vertical dimensions of the maze.
    protected int horiz;
    protected int vert;
    // Horizontal and vertical interior walls; each is true if the wall exists.
    protected boolean[][] hWalls;
    protected boolean[][] vWalls;

    // Object for generating random numbers.
    private static Random random;

    // Constants used in depth-first search (which checks for cycles in the
    // maze).
    private static final int STARTHERE = 0;
    private static final int FROMLEFT = 1;
    private static final int FROMRIGHT = 2;
    private static final int FROMABOVE = 3;
    private static final int FROMBELOW = 4;

    /**
     * Maze() creates a rectangular maze having "horizontalSize" cells in the
     * horizontal direction, and "verticalSize" cells in the vertical direction.
     * There is a path between any two cells of the maze.  A disjoint set data
     * structure is used to ensure that there is only one path between any two
     * cells.
     **/
    public Maze(int horizontalSize, int verticalSize) {
        int i, j;

        horiz = horizontalSize;
        vert = verticalSize;

        if ((horiz < 1) || (vert < 1) || ((horiz == 1) && (vert == 1))) {
            return;                                    // There are no interior walls
        }

        // Create all of the horizontal interior walls.  Initially, every
        // horizontal wall exists; they will be removed later by the maze
        // generation algorithm.
        if (vert > 1) {
            hWalls = new boolean[horiz][vert - 1];
            for (j = 0; j < vert - 1; j++) {
                for (i = 0; i < horiz; i++) {
                    hWalls[i][j] = true;
                }
            }
        }
        // Create all of the vertical interior walls.
        if (horiz > 1) {
            vWalls = new boolean[horiz - 1][vert];
            for (i = 0; i < horiz - 1; i++) {
                for (j = 0; j < vert; j++) {
                    vWalls[i][j] = true;
                }
            }
        }

        /**
         * Fill in the rest of this method.  You should go through all the walls of
         * the maze in random order, and remove any wall whose removal will not
         * create a cycle.  Use the implementation of disjoint sets provided in the
         * set package to avoid creating any cycles.
         *
         * Note the method randInt() further below, which generates a random
         * integer.  randInt() generates different numbers every time the program
         * is run, so that you can make lots of different mazes.
         **/

        int numWalls = horiz*(vert - 1) + vert*(horiz - 1);
        int[] walls = new int[numWalls];
        for (int k = 0; k< numWalls; k++) {
            walls[k] = k;
        }

        // test
//        walls[0] = 4;
//        walls[1] = 1;
//        walls[2] = 0;
//        walls[3] = 3;
//        walls[4] = 5;
//        walls[5] = 6;
//        walls[6] = 2;

        //random
        int w = numWalls;
        while (w > 1){
            int selected = randInt(w);
            int tmp = walls[w-1];
            walls[w-1] = walls[selected];
            walls[selected] = tmp;
            w--;
        }

        DisjointSets allCells = new DisjointSets(horiz*vert);
        for (int wa: walls) {
            int[] cells = findCellsOnEachside(wa);
            int c1 = allCells.find(cells[0]);
            int c2 = allCells.find(cells[1]);
            if (c1 != c2) {
                allCells.union(c1, c2);
                eliminateWalls(wa);
//                System.out.print(this);
//                System.out.println(wa);
//                System.out.println(allCells);
            }
        }
        System.out.println("Initialization done");
    }

    private void eliminateWalls(int wallIdx) {
        int[] cols = findCol(wallIdx);
        if (isVertWall(wallIdx)) {
            vWalls[cols[0]][cols[1]] = false;
        } else {
            hWalls[cols[0]][cols[1]] = false;
        }

    }

    private int[] findCol(int wallIdx) {
        int[] cols = new int[2];
        if (isVertWall(wallIdx)) {
            int x = wallIdx / vert;
            int y = wallIdx - x * vert;
            cols[0] = x;
            cols[1] = y;
        } else {
            wallIdx -= (horiz - 1) * vert;
            int y = wallIdx / horiz;
            int x = wallIdx - y * horiz;
            cols[0] = x;
            cols[1] = y;
        }
        return cols;
    }

    private int[] findCellsOnEachside(int wallIdx) {
        int[] col = findCol(wallIdx);
        int x = col[0];
        int y = col[1];
        int[] cells = new int[2];
        if (isVertWall(wallIdx)) {
            cells[0] = y * horiz + x; //left
            cells[1] = cells[0] + 1;    //right
        } else {
            cells[0] = y * horiz + x;  //up
            cells[1] = cells[0] + horiz; //down
        }
        return cells;
    }

    private boolean isVertWall(int wallIdx) {
        return wallIdx < (horiz - 1) * vert;
    }

    /**
     * toString() returns a string representation of the maze.
     **/
    public String toString() {
        int i, j;
        String s = "";

        // Print the top exterior wall.
        for (i = 0; i < horiz; i++) {
            s = s + "--";
        }
        s = s + "-\n|";

        // Print the maze interior.
        for (j = 0; j < vert; j++) {
            // Print a row of cells and vertical walls.
            for (i = 0; i < horiz - 1; i++) {
                if (vWalls[i][j]) {
                    s = s + " |";
                } else {
                    s = s + "  ";
                }
            }
            s = s + " |\n+";
            if (j < vert - 1) {
                // Print a row of horizontal walls and wall corners.
                for (i = 0; i < horiz; i++) {
                    if (hWalls[i][j]) {
                        s = s + "-+";
                    } else {
                        s = s + " +";
                    }
                }
                s = s + "\n|";
            }
        }

        // Print the bottom exterior wall.  (Note that the first corner has
        // already been printed.)
        for (i = 0; i < horiz; i++) {
            s = s + "--";
        }
        return s + "\n";
    }

    /**
     * horizontalWall() determines whether the horizontal wall on the bottom
     * edge of cell (x, y) exists.  If the coordinates (x, y) do not correspond
     * to an interior wall, true is returned.
     **/
    public boolean horizontalWall(int x, int y) {
        if ((x < 0) || (y < 0) || (x > horiz - 1) || (y > vert - 2)) {
            return true;
        }
        return hWalls[x][y];
    }

    /**
     * verticalWall() determines whether the vertical wall on the right edge of
     * cell (x, y) exists. If the coordinates (x, y) do not correspond to an
     * interior wall, true is returned.
     **/
    public boolean verticalWall(int x, int y) {
        if ((x < 0) || (y < 0) || (x > horiz - 2) || (y > vert - 1)) {
            return true;
        }
        return vWalls[x][y];
    }

    /**
     * randInt() returns a random integer from 0 to choices - 1.
     **/
    private static int randInt(int choices) {
        if (random == null) {       // Only executed first time randInt() is called
            random = new Random();       // Create a "Random" object with random seed
        }
        int r = random.nextInt() % choices;      // From 1 - choices to choices - 1
        if (r < 0) {
            r = -r;                                          // From 0 to choices - 1
        }
        return r;
    }

    /**
     * diagnose() checks the maze and prints a warning if not every cell can be
     * reached from the upper left corner cell, or if there is a cycle reachable
     * from the upper left cell.
     * <p>
     * DO NOT CHANGE THIS METHOD.  Your code is expected to work with our copy
     * of this method.
     **/
    protected void diagnose() {
        if ((horiz < 1) || (vert < 1) || ((horiz == 1) && (vert == 1))) {
            return;                                    // There are no interior walls
        }

        boolean mazeFine = true;

        // Create an array that indicates whether each cell has been visited during
        // a depth-first traversal.
        boolean[][] cellVisited = new boolean[horiz][vert];
        // Do a depth-first traversal.
        if (depthFirstSearch(0, 0, STARTHERE, cellVisited)) {
            System.out.println("Your maze has a cycle.");
            mazeFine = false;
        }

        // Check to be sure that every cell of the maze was visited.
        outerLoop:
        for (int j = 0; j < vert; j++) {
            for (int i = 0; i < horiz; i++) {
                if (!cellVisited[i][j]) {
                    System.out.println("Not every cell in your maze is reachable from " +
                            "every other cell.");
                    mazeFine = false;
                    break outerLoop;
                }
            }
        }

        if (mazeFine) {
            System.out.println("What a fine maze you've created!");
        }
    }

    /**
     * depthFirstSearch() does a depth-first traversal of the maze, marking each
     * visited cell.  Returns true if a cycle is found.
     * <p>
     * DO NOT CHANGE THIS METHOD.  Your code is expected to work with our copy
     * of this method.
     */
    protected boolean depthFirstSearch(int x, int y, int fromWhere,
                                       boolean[][] cellVisited) {
        boolean cycleDetected = false;
        cellVisited[x][y] = true;

        // Visit the cell to the right?
        if ((fromWhere != FROMRIGHT) && !verticalWall(x, y)) {
            if (cellVisited[x + 1][y]) {
                cycleDetected = true;
            } else {
                cycleDetected = depthFirstSearch(x + 1, y, FROMLEFT, cellVisited) ||
                        cycleDetected;
            }
        }

        // Visit the cell below?
        if ((fromWhere != FROMBELOW) && !horizontalWall(x, y)) {
            if (cellVisited[x][y + 1]) {
                cycleDetected = true;
            } else {
                cycleDetected = depthFirstSearch(x, y + 1, FROMABOVE, cellVisited) ||
                        cycleDetected;
            }
        }

        // Visit the cell to the left?
        if ((fromWhere != FROMLEFT) && !verticalWall(x - 1, y)) {
            if (cellVisited[x - 1][y]) {
                cycleDetected = true;
            } else {
                cycleDetected = depthFirstSearch(x - 1, y, FROMRIGHT, cellVisited) ||
                        cycleDetected;
            }
        }

        // Visit the cell above?
        if ((fromWhere != FROMABOVE) && !horizontalWall(x, y - 1)) {
            if (cellVisited[x][y - 1]) {
                cycleDetected = true;
            } else {
                cycleDetected = depthFirstSearch(x, y - 1, FROMBELOW, cellVisited) ||
                        cycleDetected;
            }
        }

        return cycleDetected;
    }

    /**
     * main() creates a maze of dimensions specified on the command line, prints
     * the maze, and runs the diagnostic method to see if the maze is good.
     */
    public static void main(String[] args) {
        int x = 50;
        int y = 15;

        /**
         *  Read the input parameters.
         */

        if (args.length > 0) {
            try {
                x = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("First argument to Simulation is not an number.");
            }
        }

        if (args.length > 1) {
            try {
                y = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Second argument to Simulation is not an number.");
            }
        }

        Maze maze = new Maze(x, y);
        System.out.print(maze);
        maze.diagnose();
    }

}
