package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;

import java.util.ArrayList;


/**
 * Provide major methods to control chess in a game
 * Only control the chessboard
 */
public class GameCore {
    private final ArrayList<Chess> chessList = new ArrayList<>();
    private final MoveHistory moveHistory = new MoveHistory();

    /**
     * This method will set all chess to the beginning position
     */
    public void initialGame() {

    }

    /**
     * This method will set all chess to the given chessList
     *
     */
    public void setChessList() {

    }


    /**
    * Target a chess move to the position, return false if not available
    */
    public boolean moveChess(Chess chess, Position targetPos) {

    }

    /**
     * Target a chess move from a position (if has any) to a new position, return false if not available
     */
    public boolean moveChess(Position chessPos, Position targetPos) {

    }

    /**
     * Target a pawn to move forward and update to a certain chess type
     */
    public boolean movePawnPromotion(Chess pawn, ChessType updateType) {

    }


    /**
     * reverseMove, return the reversed move
     */
    public Move reverseMove() {

    }


    /**
     * Get the chess at the position
     * May return null if not chess at the position
     */
    public Chess getChess(Position position) {
        for (Chess chess : chessList) {
            if (chess.getPosition().equals(position))
                return chess;
        }
        return null;
    }

    /**
     * Get a list of all the chess copy in game
     */
    public ArrayList<Chess> getChessList() {
        //For safe reason, return a copy of the list
        return new ArrayList<>(this.chessList);
    }

    /**
     * Return a list of all available positions of a chess
     */
    public ArrayList<Position> getAvailableMove(Chess chess) {

    }

    /**
     * Return a list of different color chess that will target the position
     */
    public ArrayList<Chess> getEnemy(Position position) {

    }

    /**
     * Return a list of the same color chess that will protect the position
     */
    public ArrayList<Chess> getAlly(Position position) {

    }

    /**
     * Return if the position is available for the chess
     */
    public boolean isMoveAvailable(Chess chess, Position targetPos) {

    }

    public boolean isMoveAvailable(Move move) {

    }
}
