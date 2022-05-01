package edu.sustech.chessking.gameLogic;

public class Player {
    String chessSkin;
    public boolean hasChessSkin = false;
    String boardTheme;
    String backgroundTheme;
    String avatar;
    public boolean hasAvatar = false;
    String name;
    String password;

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Player(String name) {
        this.name = name;
        this.password = null;
    }

    public void setChessSkin(String chessSkin) {
        hasChessSkin = true;
        this.chessSkin = chessSkin;
    }

    public void deleteChessSkin() {
        hasChessSkin = false;
        this.chessSkin = null;
    }

    public void setBoardTheme(String boardTheme) {
        this.boardTheme = boardTheme;
    }

    public void setBackgroundTheme(String backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    public void setAvatar(String avatar) {
        hasAvatar = true;
        this.avatar = avatar;
    }

    public void deleteAvatar(){
        this.hasAvatar = false;
        this.avatar = null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChessSkin() {
        return chessSkin;
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

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
