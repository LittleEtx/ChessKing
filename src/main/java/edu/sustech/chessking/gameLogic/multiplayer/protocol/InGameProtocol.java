package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import javafx.geometry.Point2D;

public class InGameProtocol {
    public static double[] toDouble(Point2D pt) {
        double[] doubleList = new double[2];
        doubleList[0] = pt.getX();
        doubleList[1] = pt.getY();
        return doubleList;
    }

    public static Point2D toPoint2D(double[] doubleList) {
        return new Point2D(doubleList[0], doubleList[1]);
    }


    //every bundle relating to player action should contain the color msg
    public static final String Mouse = "mouse";
    //transfer the mouse position
    public static final String Color = "color";

    //player action
    public static final String PickUpChess = "pickUpChess"; //Chess
    public static final String PutDownChess = "putDownChess"; //empty
    public static final String MoveChess = "moveChess"; //Move
    public static final String RequestReverse = "requestReverse"; //empty
    public static final String AllowReverse = "allowReverse"; //empty

    //game getter action
    public static final String GetMoveHistory = "getMoveHistory"; //empty
    public static final String MoveHistory = "moveHistory"; //String
    public static final String GetTurn = "getTurn"; //empty
    public static final String Turn = "turn"; //ColorType
    public static final String GetGameInfo = "getGameInfo";
}
