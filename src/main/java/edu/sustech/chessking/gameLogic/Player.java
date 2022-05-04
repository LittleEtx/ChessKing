package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

public class Player {
    private int score;
    private String name;
    private String avatar;
    private String chessSkin;
    private String boardSkin;
    private String background;
    private String password;

    /**
     * default constructor
     */
    public Player() {
        score = 0;
        name = "";
        avatar = "avatar7";
        chessSkin = "default";
        boardSkin = "default";
        background = "default";
        password = null;
    }

    public Player(String playerMessage) {
        String[] data = playerMessage.split(" ");
        if (data.length < 6)
            throw new ConstructorException("Invalid message number");

        score = Integer.parseInt(data[0]);
        name = data[1];
        avatar = data[2];
        chessSkin = data[3];
        boardSkin = data[4];
        background = data[5];
        if (data.length == 7)
            password = data[6];
        else
            password = null;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public void setName(String name) {
        if (name == null || name.isEmpty() ||
                name.contains(" ") ||
                name.contains("\\") ||
                name.contains("/") ||
                name.contains(":") ||
                name.contains("*") ||
                name.contains("?") ||
                name.contains("\"") ||
                name.contains("<") ||
                name.contains(">") ||
                name.contains("|"))
            throw new IllegalArgumentException("Name should not contains" +
                    " the blank and \\/:*?\"<>|");
        this.name = name;
    }

    public void setChessSkin(String chessSkin) {
        if (chessSkin == null)
            throw new IllegalArgumentException("chessSkin can't be null!");
        this.chessSkin = chessSkin;
    }

    public void setBoardSkin(String boardSkin) {
        if (chessSkin == null)
            throw new IllegalArgumentException("boardSkin can't be null!");
        this.boardSkin = boardSkin;
    }

    public void setBackground(String background) {
        if (chessSkin == null)
            throw new IllegalArgumentException("background can't be null!");
        this.background = background;
    }

    public void setAvatar(String avatar) {
        if (chessSkin == null)
            throw new IllegalArgumentException("avatar can't be null!");
        this.avatar = avatar;
    }

    public void setPassword(String password) {
        if (password.isEmpty() || password.contains(" "))
            throw new IllegalArgumentException("password should not " +
                    "contains the blank");
        this.password = password;
    }

    //getter method

    public int getScore() {
        return score;
    }

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
        sb.append(score).append(" ");
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
