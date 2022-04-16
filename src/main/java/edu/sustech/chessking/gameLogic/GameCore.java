package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;


/**
 * Provide major methods to control chess in a game
 * Only control the chessboard
 */
public class GameCore {
    private final ArrayList<Chess> chessList = new ArrayList<>();
    private final MoveHistory moveHistory = new MoveHistory();

    //===============================
    //    ChessBoard Setting Method
    //===============================
    /**
     * This method will set all chess to the beginning position
     */
    public void initialGame() {
        chessList.add(new Chess("white pawn A2"));


    }

    /**
     * This method will set all chess to a given state by game history
     */
    public boolean setGame(MoveHistory history) {


        //

        return true;
    }

    /**
     * Get a copy of current history
     */
    public MoveHistory getGameHistory() {
        return moveHistory.clone();
    }

    /**
     * This method will set all chess to a given state
     * Waning: this action will clear game history, be careful
     */
    public void setGame(ArrayList<Chess> chessList) {

    }

    //==================================
    //      Game Checking Method
    //==================================
    /**
     * see if one side has lost, including:
     * 1. No king on the chessboard
     * 2. Is being checked but no way to prevent this
     */
    public boolean hasLost(ColorType side) {

        //Needs to add

        return false;
    }

    /**
     * see if one side has wined
     */
    public boolean hasWined(ColorType side) {
         if (side == ColorType.BLACK)
             return hasLost(ColorType.WHITE);
         else
             return hasLost(ColorType.BLACK);
     }

    /**
     * see if it has drawn at the time, including:
     * 1. One side has no move to go
     * 2. The same situation appears for the third time
     * 3. Not pawn was moved and no chess was eaten for the first 50 moves
     * 4. Some special ending situation where both side can not win
     */
    public boolean hasDrawn() {

        //Needs to add

        return false;
    }

    /**
     * see if one side has be checked
     */
    public boolean isChecked(ColorType side) {

        //Needs to add

        return false;
    }

    /**
     * see if one side is checking
     */
    public boolean isChecking(ColorType side) {
        if (side == ColorType.WHITE)
            return isChecked(ColorType.BLACK);
        else
            return isChecked(ColorType.WHITE);
    }

    //==================================
    //        Chess Moving Method
    //==================================
    /**
    * Target a chess move to the position, return false if not available
    */
    public boolean moveChess(Chess chess, Position targetPos) {

        //Needs to add

        return false;
    }

    /**
     * Target a chess move from a position (if has any) to a new position, return false if not available
     */
    public boolean moveChess(Position chessPos, Position targetPos) {

        //Needs to add

        return false;
    }

    /**
     * Target a pawn to move forward and update to a certain chess type
     * Note that if the pawn is to promote, then moveChess method will return false
     */
    public boolean movePawnPromotion(Chess pawn, ChessType updateType) {

        //Needs to add

        return false;
    }


    /**
     * reverseMove, return the reversed move
     */
    public Move reverseMove() {

        //Needs to add

        return null;
    }

    /**
     * Return if the position is available for the chess
     */
    public boolean isMoveAvailable(Chess chess, Position targetPos) {

        //Needs to add

        return false;
    }

    public boolean isMoveAvailable(Move move) {

        //Needs to add


        return false;
    }


    //==================================
    //       Chess getting Method
    //==================================
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
     * Return a list of all available move positions of a chess
     */
    public ArrayList<Position> getAvailablePosition(Chess chess) {



        return null;
    }

    /**
     * Return a list of all available moves of a chess
     */
    public ArrayList<Move> getAvailableMove(Chess chess) {

        //Needs to add

        return null;
    }

    /**
     * Return a list of different color chess that will target the position
     */
    public ArrayList<Chess> getEnemy(Position position) {

        //Needs to add

        return  null;
    }

    /**
     * Return a list of the same color chess that will protect the position
     */
    public ArrayList<Chess> getAlly(Position position) {

        //Needs to add

        return null;
    }
}
