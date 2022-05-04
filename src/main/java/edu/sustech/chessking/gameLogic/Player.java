package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;
import javafx.scene.paint.Color;

public class Player {
    private int score;
    private String name;
    private String avatar;
    private String chessSkin;
    private String boardSkin;
    private String background;
    private String password;
    private Color color1;
    private Color color2;

    /**
     * default constructor
     */
    public Player() {
        score = 0;
        name = "";
        avatar = "avatar7";
        chessSkin = "default";
        boardSkin = "default";
        background = "apple";
        password = null;
        setColor("wooden");
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

    public void setColor(String color){
        if(color.equals("default")){
            this.color1 = Color.GREEN;
            this.color2 = Color.LIGHTGOLDENRODYELLOW;
        }else if(color.equals("transparent")){
            this.color1 = Color.web("#00000070");
            this.color2 = Color.web("#00000050");
        }else if(color.equals("wooden")){
            this.color1 = Color.web("#A52A2A");
            this.color2 = Color.web("#FFFACD");
        }
    }

    public void setColor1(Color color){
        this.color1 = color;
    }

    public void setColor2(Color color){
        this.color2 = color;
    }

    public Color getColor1(){
        return this.color1;
    }

    public Color getColor2(){
        return this.color2;
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
