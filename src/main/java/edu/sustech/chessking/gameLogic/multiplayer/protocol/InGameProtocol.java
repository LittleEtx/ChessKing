package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import javafx.geometry.Point2D;

public class InGameProtocol {
    //every bundle should contain the color msg
    public static final String Color = "color";

    public static double[] toDouble(Point2D pt) {
        double[] doubleList = new double[2];
        doubleList[0] = pt.getX();
        doubleList[1] = pt.getY();
        return doubleList;
    }

    public static Point2D toPoint2D(double[] doubleList) {
        return new Point2D(doubleList[0], doubleList[1]);
    }


    //transfer the mouse position
    public static final String Mouse = "mouse";

    //player action
    public static final String PickUpChess = "pickUpChess"; //Chess
    public static final String PutDownChess = "putDownChess"; //none
    public static final String MoveChess = "moveChess"; //Move
}
