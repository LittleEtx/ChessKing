package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

public class Player {
    String name;
    String avatar;
    String chessSkin;
    String boardSkin;
    String background;
    String password;

    /**
     * default constructor
     */
    public Player() {
        name = "";
        avatar = "avatar7";
        chessSkin = "default";
        boardSkin = "default";
        background = "default";
        password = null;
    }

    public Player(String playerMessage) {
        String[] data = playerMessage.split(" ");
        if (data.length < 5)
            throw new ConstructorException("Invalid message number");

        name = data[0];
        avatar = data[1];
        chessSkin = data[2];
        boardSkin = data[3];
        background = data[4];
        if (data.length == 6)
            password = data[5];
        else
            password = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChessSkin(String chessSkin) {
        this.chessSkin = chessSkin;
    }

    public void setBoardSkin(String boardSkin) {
        this.boardSkin = boardSkin;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //getter method
    public String getName() {
        return name;
    }

    public String getChessSkin() {
        return chessSkin;
    }

    public String getBoardSkin() {
        return boardSkin;
    }

    public String getBackground() {
        return background;
    }

    public String getAvatar() {
        return avatar;
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
        sb.append(background);
        if (password != null)
            sb.append(" ").append(password);
        return sb.toString();
    }
}
