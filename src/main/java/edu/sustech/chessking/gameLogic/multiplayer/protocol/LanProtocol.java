package edu.sustech.chessking.gameLogic.multiplayer.protocol;

public class LanProtocol {
    //ip and port for broadcasting server info
    public static final String Address = "224.0.2.60";
    public static final int Port = 4444;

    //client side
    public static final String HasGame = "hasGame"; //empty
    public static final String JoinGame = "joinGame"; //Player

    public static final String JoinView = "joinView"; //empty
    public static final String Quit = "quit"; //empty


    //server side
    public static final String SendGameInfo = "sendGameInfo"; //GameInfo
    public static final String SuccessfullyJoinIn = "successfullyJoinIn"; //empty
    public static final String FailToJoin = "failToJoinIn"; //String msg
    public static final String StartGame = "startGame"; //Player : white player
}
