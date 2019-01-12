package player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;

/**
 * Author: bingxing.k
 * Date: 18-11-13
 */


public class Board {
    private final static int EMPTY = -1;
    private final static int WHITE = 1;
    private final static int BLACK = 0;
    private final static int MAXCHIPSCOUNT = 10;
    private final static int NETWORK_LEAST_CHIPS = 6;


    private final static int WIN = 10000;
    private final static int LOSE = 0 - WIN;

    private final static int WIDTH = 8; // 8*8 play board
    private int[][] playBoard;
    private int whiteCount = 0;
    private int blackCount = 0;

    public Board() {
        playBoard = new int[WIDTH][WIDTH];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                playBoard[i][j] = EMPTY;
            }
        }
    }

    public Board copy() {
        Board b = new Board();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                b.set(i,j, playBoard[i][j]);
            }
        }
        return b;
    }

    @Override
    public String toString() {
        String s = "                Play Board\n  |" +
                "---------------------------------------|\n";
        String c;
        for (int j = 0; j < WIDTH; j++) {
            s += Integer.toString(j) + " |";
            for (int i = 0; i < WIDTH; i++) {
                c = "    |";
                if (playBoard[i][j] == WHITE) {
                    c = " WW |";
                } else if (playBoard[i][j] == BLACK) {
                    c = " BB |";
                } else if (playBoard[i][j] == 11) {
                    c = " XX |";
                } else if (playBoard[i][j] == 14) {
                    c = " OO |";
                }
                s += c;
            }
            s += "\n  |---------------------------------------|\n";
        }
        s += "     0    1    2    3    4    5    6    7\n";
        return s;
    }

    /*
    test methods : set,
     */

    public void set(int x, int y, int color) {
        playBoard[x][y] = color;
    }

    private boolean isWhiteGoalArea(int x, int y) {
        return isValidChip(x, y) && (x == 0 || x == WIDTH - 1) && (x != y);
    }

    private boolean isBlackGoalArea(int x, int y) {
        return isValidChip(x, y) && (y == 0 || y == WIDTH - 1) && (x != y);
    }

    private int chipHasInWhiteStartGoal() {
        int cnt = 0;
        int x = 0;
        for (int y=0; y<WIDTH; y++) {
            if(playBoard[x][y] == WHITE) {
                cnt++;
            }
        }
        return cnt;
    }

    private int chipIsInWhiteEndGoal() {
        int cnt = 0;
        int x = WIDTH-1;
        for (int y=0; y<WIDTH; y++) {
            if(playBoard[x][y] == WHITE) {
                cnt++;
            }
        }
        return cnt;
    }

    private int chipIsInBlackStartGoal() {
        int cnt = 0;
        int y = 0;
        for (int x=0; x<WIDTH; x++) {
            if(playBoard[x][y] == BLACK) {
                cnt++;
            }
        }
        return cnt;
    }

    private int chipIsInBlackEndGoal() {
        int cnt = 0;
        int y = WIDTH-1;
        for (int x=0; x<WIDTH; x++) {
            if(playBoard[x][y] == BLACK) {
                cnt++;
            }
        }
        return cnt;
    }

    private boolean isOppositeGoalArea(int x, int y, int color) {
        if (color == BLACK) {
            return isWhiteGoalArea(x, y);
        } else {
            return isBlackGoalArea(x, y);
        }
    }

    private boolean isValidChip(int x, int y) {
        return !(x > WIDTH - 1 || y > WIDTH - 1 || x < 0 || y < 0 ||
                (x == 0 && y == 0) || (x == 0 && y == WIDTH - 1) || (y == 0 && x == WIDTH - 1) || (x == WIDTH - 1 && y == WIDTH - 1));
    }

    private boolean isOccupied(int x, int y) {
        return playBoard[x][y] != EMPTY;
    }

    private boolean isValidChipForPlayer(int x, int y, int color) {
        if (!isOccupied(x, y)) {
            if (color == WHITE) {
                return isWhiteGoalArea(x, y);
            } else if (color == BLACK) {
                return isBlackGoalArea(x, y);
            }
        }
        return false;
    }

    /*
    Basic rules:
      1)  No chip may be placed in any of the four corners.
      2)  No chip may be placed in a goal of the opposite color.
      3)  No chip may be placed in a square that is already occupied.
     */
    private boolean isValidFor3BasicRules(int x, int y, int color) {
        return isValidChip(x, y) && !isOccupied(x, y) && !isOppositeGoalArea(x, y, color);
    }

    /*
    A copy of the current play board
     */
    private int[][] getPlayBoardCp() {
        int[][] cp = new int[WIDTH][WIDTH];
        for (int i = 0; i < WIDTH; i++) {
            System.arraycopy(playBoard[i], 0, cp[i], 0, WIDTH);
        }
        return cp;
    }

    /*
    4th Rule:
      ```A player may not have more than two chips in a connected group, whether
      connected orthogonally or diagonally.```

      For a new move to (x, y), checking:
      1) if there are more than 2 neighbours are in the same color
      2) if any neighbour is in a connection group
      * neighbours are at most WIDTH chips around (x, y)
     */
    private boolean isValidFor4thRule(int x, int y, int color, int[][] playBoard) {
        boolean pass = true;
        int cnt = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // except for invalid neighbours && myself
                if (isValidChip(i, j) && !(x == i && y == j)) {
                    if (playBoard[i][j] == color) {
                        pass = pass && isGoodNeighbour(i, j, x, y, color, playBoard);
                        cnt++;
                    }
                }
                if (!pass) {
                    return false;
                }
            }
        }
        return cnt < 2;
    }

    /**
     * A good neighbour is one chip not in a connection group with `other` neighbour chips
     */

    private boolean isGoodNeighbour(int neighbourX, int neighbourY, int myX, int myY, int color, int[][] playBoardCopy) {
        for (int i = neighbourX - 1; i <= neighbourX + 1; i++) {
            for (int j = neighbourY - 1; j <= neighbourY + 1; j++) {
                // except for the querying neighbour and itself
                if ((i == myX && j == myY) || (i == neighbourX && j == neighbourY)) {
                    continue;
                }
                if (isValidChip(i, j) && playBoardCopy[i][j] == color) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getChipCount(int color) {
        switch (color) {
            case BLACK:
                return blackCount;
            case WHITE:
                return whiteCount;
            default:
                throw new RuntimeException();
        }
    }

    private boolean isFullForAddMoves(int color) {
        return getChipCount(color) == MAXCHIPSCOUNT;
    }

    private boolean checkAddMove(int x, int y, int color, int[][] playBoardCopy) {
        return isValidFor3BasicRules(x, y, color) && isValidFor4thRule(x, y, color, playBoardCopy);
    }


    public boolean checkMove(Move m, int color) {
        int[][] playBoardCp = getPlayBoardCp();
        if (m.moveKind == Move.ADD) {
            if (isFullForAddMoves(color)) {
                return false;
            }
            playBoardCp[m.x1][m.y1] = color;
            return checkAddMove(m.x1, m.y1, color, playBoardCp);
        } else if (m.moveKind == Move.STEP) {
            playBoardCp[m.x2][m.y2] = EMPTY;
            playBoardCp[m.x1][m.y1] = color;
            return checkAddMove(m.x1, m.y1, color, playBoardCp);
        } else if (m.moveKind == Move.QUIT) {
            return true;
        }
        return false;
    }


    protected void moveMove(Move m, int color) {
        if (m.moveKind == Move.ADD) {
            playBoard[m.x1][m.y1] = color;
            if (color == WHITE) {
                whiteCount++;
            } else {
                blackCount++;
            }
        } else if (m.moveKind == Move.STEP) {
            playBoard[m.x2][m.y2] = EMPTY;
            playBoard[m.x1][m.y1] = color;
        } else if (m.moveKind == Move.QUIT) {
            ;
        }
    }

    /**
     * A test method to implement a game
     */

    protected Move findAMove(int color) {
        List<Move> addMoves = findAllValidAddMoves(color);
        List<Move> stepMoves = findAllValidStepMoves(color);

        int size;
        if (getChipCount(color) < MAXCHIPSCOUNT) {
            size = addMoves.size();
            return addMoves.get(ThreadLocalRandom.current().nextInt(0, size));
        } else {
            size = stepMoves.size();
            return stepMoves.get(ThreadLocalRandom.current().nextInt(0, size));
        }
    }


    public List<Move> findAllValidMoves(int color) {
        List<Move> result;
        if (getChipCount(color) < MAXCHIPSCOUNT) {
            result = findAllValidAddMoves(color);
        } else {
            result = findAllValidStepMoves(color);
        }
        Collections.shuffle(result);
        return result;
    }

    private ArrayList<Move> findAllValidAddMoves(int color) {
        Move m;
        ArrayList<Move> movesList = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                m = new Move(i, j);
                if (checkMove(m, color)) {
                    movesList.add(m);
                }
            }
        }
        return movesList;
    }

    private ArrayList<Move> findAllValidStepMoves(int color) {
        ArrayList<Move> movesList = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (playBoard[i][j] == color) {
                    ArrayList<Move> movesforij = findStepMoveForOneChip(i, j, color);
                    if (!movesforij.isEmpty()) {
                        movesList.addAll(movesforij);
                    }
                }
            }
        }
        return movesList;
    }

    private ArrayList<Move> findStepMoveForOneChip(int x, int y, int color) {
        ArrayList<Move> movesList = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (isValidChipForPlayer(i, j, color)) {
                    Move m = new Move(i, j, x, y);
                    if (checkMove(m, color)) {
                        movesList.add(m);
                    }
                }
            }
        }
        return movesList;
    }

    /**
     * Q3: finding the chips (of the same color) that form connections with a chip
     */

    public ArrayList<Chip> findAllChips(int x, int y) {
        return findAllChips(playBoard[x][y]);
    }

    public ArrayList<Chip> findAllChips(int color) {
        if (color == EMPTY) {
            return null;
        }
        ArrayList<Chip> chips = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (playBoard[i][j] == color) {
                    chips.add(new Chip(i, j, color));
                }
            }
        }
        return chips;
    }

    public ArrayList<Chip> findAllChipsFormingAConnection(int x, int y, int color) {
        ArrayList<Chip> chips = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (isValidChip(i, j) && isConnectable(x, y, i, j) && playBoard[i][j] == color) {
                    chips.add(new Chip(i, j, playBoard[i][j]));
                }
            }
        }
        return chips;
    }

    private ArrayList<Chip> findAllOtherChipsConnectToSourceChip(Chip sourceChip, ArrayList<Chip> otherChips) {
        ArrayList<Chip> chips = new ArrayList<>();
        for (Chip c : otherChips) {
            if (isConnectable(sourceChip.getX(), sourceChip.getY(), c.getX(), c.getY())) {
                chips.add(c);
            }
        }
        return chips;
    }

    private boolean isConnectable(int x1, int y1, int x2, int y2) {
        if ((x1 == x2 && y1 == y2) || (isBlockedInPath(x1, y1, x2, y2))) {
            return false;
        }
        return twoPointsInSameLine(x1, y1, x2, y2);
    }

    private boolean isBlockedInPath(int x1, int y1, int x2, int y2) {
        int startX = x1 <= x2 ? x1 : x2;
        int endX = x1 > x2 ? x1 : x2;
        int startY = y1 <= y2 ? y1 : y2;
        int endY = y1 > y2 ? y1 : y2;
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                if ((i == x1 && j == y1) || (i == x2 && j == y2)) {
                    // skip the start and end chips
                } else if (threePointsInSameLine(x1, y1, x2, y2, i, j) && isOccupied(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    // helper method
    private boolean twoPointsInSameLine(int x1, int y1, int x2, int y2) {
        return x1 == x2 || y1 == y2 || Math.abs(y1 - y2) == Math.abs(x1 - x2);
    }

    // helper method
    private boolean threePointsInSameLine(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x1 == x2 && x1 == x3) || (y1 == y2 && y1 == y3) || (Math.abs(y1 - y2) == Math.abs(x1 - x2) && Math.abs(y1 - y3) == Math.abs(x1 - x3));
    }

    public Connection DFS(Chip c, ArrayList<Chip> all) {
        all.remove(c);
        Connection root = new Connection(c);
        ArrayList<Chip> others = findAllOtherChipsConnectToSourceChip(c, all);
        for (Chip o : others) {
            @SuppressWarnings("unchecked")
            ArrayList<Chip> allCLone = (ArrayList<Chip>) all.clone();
            root.addChild(DFS(o, allCLone));
        }
        return root;
    }


    /**
     * Q4: determining whether a game board contains any networks for a given
     * player (very difficult; put your smartest teammate on this),
     */

    public int whoWins() {
        if (isNetworkAppears(BLACK)) {
            return BLACK;
        }
        if (isNetworkAppears(WHITE)) {
            return WHITE;
        }
        // no Body won in this turn
        return -1;
    }

    public boolean isNetworkAppears(int color) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < WIDTH; i++) {
            if (color == WHITE) {
                y = i;
            } else {
                x = i;
            }
            if (isOccupied(x, y)) {
                Chip c = new Chip(x, y);
                ArrayList<Chip> chips = findAllChips(color);
                Connection root = DFS(c, chips);
                ArrayList<ArrayList<Chip>> allConLists = root.traverse();
                for (ArrayList<Chip> conList : allConLists) {
                    if (conList.size() >= NETWORK_LEAST_CHIPS && isNetwork(conList)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNetwork(ArrayList<Chip> conList) {
        int idx = 0;
        int len = conList.size();
        boolean endGoalArea = false;
        // ROUND ONE: the first 6 chips can not have two startgoalchips, and if, only one endgoalchip in 6th chip
        while (idx < NETWORK_LEAST_CHIPS) {
            if (conList.get(idx).inEndGoalArea() && idx != NETWORK_LEAST_CHIPS - 1) {
                return false;
            }
            if (conList.get(idx).inStartGoalArea() && idx != 0) {
                return false;
            }
            idx++;
        }

        // ROUND TWO: a netword have least 6 chips with one chip after 0-idx in end-goal-area.
        idx = 0;
        while (idx <= len - 3) {
            endGoalArea = conList.get(idx).inEndGoalArea();
            if (threePointsInSameLine(
                    conList.get(idx).getX(), conList.get(idx).getY(),
                    conList.get(idx + 1).getX(), conList.get(idx + 1).getY(),
                    conList.get(idx + 2).getX(), conList.get(idx + 2).getY()
            )) {
                return false;
            }
            // do not need finish the traverse, if meets all the conditions. return true
            if (endGoalArea && idx >= NETWORK_LEAST_CHIPS) {
                return true;
            }
            idx++;
        }
        endGoalArea = conList.get(idx).inEndGoalArea();
        endGoalArea = endGoalArea || conList.get(idx + 1).inEndGoalArea();
        return endGoalArea;
    }

    /**
     * 5)  computing an evaluation function for a board (possibly difficult), and
     */

    public int evalMove(Move m, int side) {
        int opponent = side == WHITE? BLACK: WHITE;
        Board board = this.copy();
        board.moveMove(m, side);

        int score = board.initScore(side);
        int isAnybodyWin = board.whoWins();;

        if (isAnybodyWin > -1)
        {
            score = isAnybodyWin == side? WIN: LOSE;
        } else {
            score = score + scoreDependsOnConnections(side) - scoreDependsOnConnections(opponent);
        }
        return score;
    }


    private int initScore(int color) {
        int start, end;
        if (color == WHITE) {
            start = chipHasInWhiteStartGoal();
            end = chipIsInWhiteEndGoal();
        } else {
            start = chipIsInBlackStartGoal();
            end = chipIsInBlackEndGoal();
        }
        return _goalAreaScore(start, end);
    }


    private int _goalAreaScore(int start, int end) {
        // at most, 6 chips in goal area, if still want to win
        if (start == 0 && end == 0 ) {
            return 0;
        }
        if (start + end > 6) {
            return LOSE;
        }
        if (start > 0 && end > 0) {
            return 6 - start - end;
        }
        return 3-start < 3-end ? 3-start:3-end;
    }

    private int scoreDependsOnConnections(int color) {
        int cnt = 0;
        ArrayList<Chip> chips = findAllChips(color);
        for (Chip c: chips) {
            @SuppressWarnings("unchecked")
            ArrayList<Chip> all = (ArrayList<Chip>) chips.clone();
            cnt += DFS(c, all).traverse().size();
        }
        return cnt;
    }
}

