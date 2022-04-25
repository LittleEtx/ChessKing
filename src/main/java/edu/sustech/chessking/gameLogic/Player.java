package edu.sustech.chessking.gameLogic;

public class Player {
    String chessTheme;
    String boardTheme;
    String backgroundTheme;
    String avatar;
    String id;
    String name;
    String password;

    public Player(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.password = null;
    }

    public void setChessTheme(String chessTheme) {
        this.chessTheme = chessTheme;
    }

    public void setBoardTheme(String boardTheme) {
        this.boardTheme = boardTheme;
    }

    public void setBackgroundTheme(String backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChessTheme() {
        return chessTheme;
    }

    public String getBoardTheme() {
        return boardTheme;
    }

    public String getBackgroundTheme() {
        return backgroundTheme;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
