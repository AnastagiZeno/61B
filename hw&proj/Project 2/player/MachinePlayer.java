/* MachinePlayer.java */

package player;

import java.util.List;

/**
 * An implementation of an automatic Network player.  Keeps track of moves
 * made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

    Board playBoard;
    int me;
    int enemy;
    int searchDepth;
    int moves_count = 0;

    // Creates a machine player with the given color.  Color is either 0 (black)
    // or 1 (white).  (White has the first move.)
    public MachinePlayer(int color) {
        me = color;
        enemy = color == 0? 1:0;
        searchDepth = 1;
        playBoard = new Board();
    }

    // Creates a machine player with the given color and search depth.  Color is
    // either 0 (black) or 1 (white).  (White has the first move.)
    public MachinePlayer(int color, int searchDepth) {
        me = color;
        enemy = color == 0? 1:0;
        searchDepth = searchDepth;
        playBoard = new Board();
    }

    // Returns a new move by "this" player.  Internally records the move (updates
    // the internal game board) as a move by "this" player.
    public Move chooseMoveRandom() {
        Move m = playBoard.findAMove(me);
        playBoard.moveMove(m, me);
        return m;
    }

    public Move chooseMove() {
        List<Move> moveList = playBoard.findAllValidMoves(me);
        Move bestMove = new Move();
        int bestScore = -1000;
        int score;
        for (Move m: moveList) {
            score = playBoard.evalMove(m, me);
            System.out.print("Score[" + Integer.toString(score) + "]   ");
            System.out.println(m);
            if (score > bestScore) {
                bestMove = m;
                bestScore = score;
            }
        }
        playBoard.moveMove(bestMove, me);
        return bestMove;
    }


    private Move chooseBestMoveInOneTurn(int color) {
        List<Move> moveList = playBoard.findAllValidMoves(color);
        Move bestMove = new Move();
        int bestScore = -1000;
        int score;
        for (Move m: moveList) {
            score = playBoard.evalMove(m, color);
            if (score > bestScore) {
                bestMove = m;
                bestScore = score;
            }
        }
        return bestMove;
    }


    private Best maxminChooseMove(int side, Board board, int depth) {
        Best myBest = new Best();
        Best reply;

        if (depth == 0) {

        }

        if (side == me) {
            myBest.score = -1000;
        } else {
            myBest.score = 1000;
        }

        myBest.move = board.findAMove(side);
        List<Move> moveList = board.findAllValidMoves(side);
        for (Move m : moveList) {
            Board board2 = playBoard.copy();
            board2.moveMove(m, side);
            reply = maxminChooseMove(opponent(side), board2, depth-1);

            if ( (side == me && reply.score > myBest.score) || (side == enemy && reply.score < myBest.score) ){
                myBest.move = m;
                myBest.score = reply.score;
            }
        }
        return myBest;
    }

    private int opponent(int side) {
        return side == 0? 1:0;
    }


    // If the Move m is legal, records the move as a move by the opponent
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method allows your opponents to inform you of their moves.
    public boolean opponentMove(Move m) {
        if (playBoard.checkMove(m, enemy)) {
            playBoard.moveMove(m, enemy);
            return true;
        }
        return false;
    }

    // If the Move m is legal, records the move as a move by "this" player
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method is used to help set up "Network problems" for your
    // player to solve.
    public boolean forceMove(Move m) {
        if (playBoard.checkMove(m, me)) {
            playBoard.moveMove(m, me);
            return true;
        }
        return false;
    }

    /*************************************************************************************************
     * test
     * @param x
     * @param y
     * @param color
     *************************************************************************************************/
    public void ForTest_playboard_set(int x, int y, int color) {
        playBoard.set(x, y, color);
    }

    public Board getPlayBoard(){
        return playBoard;
    }

}
