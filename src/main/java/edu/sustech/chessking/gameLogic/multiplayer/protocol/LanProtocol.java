package edu.sustech.chessking.gameLogic.multiplayer.protocol;

public class LanProtocol {
    //client side
    public static final String HasGame = "hasGame"; //empty
    public static final String JoinGame = "joinGame"; //Player

    public static final String JoinView = "joinView"; //empty
    public static final String Quit = "quit"; //empty


    //server side
    public static final String SendGameInfo = "sendGameInfo"; //GameInfo
    public static final String SuccessfullyJoinIn = "successfullyJoinIn"; //empty
    public static final String FailToJoinInfo = "failToJoinIn"; //String msg
    public static final String StartGame = "startGame"; //
}
