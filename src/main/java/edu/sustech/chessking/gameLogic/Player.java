package edu.sustech.chessking.gameLogic;

public class Player {
    String chessSkin;
    public boolean hasChessSkin = false;
    String boardSkin;
    String background;
    String avatar;
    public boolean hasAvatar = false;
    String name;
    public boolean hasName = false;
    String password;

    public Player(String name, String password) {
        this.name = name;
        hasName = true;
        this.password = password;
        chessSkin = "default";
        boardSkin = "default";
        background = "default";
        avatar = "default";
    }

    public Player(String name) {
        this(name, null);
        hasName = true;
    }

    public void deleteName(){
        this.name = null;
        hasName = false;
    }

    public void setChessSkin(String chessSkin) {
        hasChessSkin = true;
        this.chessSkin = chessSkin;
    }

    public void deleteChessSkin() {
        hasChessSkin = false;
        this.chessSkin = null;
    }

    public void setBoardSkin(String boardSkin) {
        this.boardSkin = boardSkin;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public String getboardSkin() {
        return boardSkin;
    }

    public String getbackground() {
        return background;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        sb.append(avatar).append(" ");
        sb.append(chessSkin).append(" ");
        sb.append(boardSkin).append(" ");
        sb.append(background).append(" ");
        return sb.toString();
    }
}
