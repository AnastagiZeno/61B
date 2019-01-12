package player;

/**
 * Author: Anastagizeno
 * Date: 18-11-21 下午4:36
 */


public class Chip {
    private final static int WIGHT = 8;
    private final static int EMPTY = -1;
    private final static int WHITE = 1;
    private final static int BLACK = 0;
    private int x, y, color;

    public Chip(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Chip(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = EMPTY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public boolean inStartGoalArea() {
        if (color == BLACK) {
            return y == 0;
        }
        return x == 0;
    }

    public boolean inEndGoalArea() {
        if (color == BLACK) {
            return y == WIGHT - 1;
        }
        return x == WIGHT - 1;
    }

    @Override
    public String toString() {
        String c;
        switch (this.color) {
            case BLACK:
                c = "B";
                break;
            case WHITE:
                c = "W";
                break;
            default:
                c = "E";
                break;
        }
        return "(" + Integer.toString(x) + ", " + Integer.toString(y) + ", " + c + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Chip) {
            if (((Chip) obj).getX() == x && ((Chip) obj).getY() == y && ((Chip) obj).getColor() == color ) {
                return true;
            }
        }
        return false;
    }
}
