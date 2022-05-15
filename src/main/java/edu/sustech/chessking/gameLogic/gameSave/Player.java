package edu.sustech.chessking.gameLogic.gameSave;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;
import javafx.scene.paint.Color;

import java.io.Serial;
import java.io.Serializable;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int score;
    private String name;
    private String avatar;
    private String chessSkin;
    private String background;
    private Color color1;
    private Color color2;

    private static final Color defaultColor1 = Color.web("#00000070");
    private static final Color defaultColor2 = Color.web("#00000050");

    /**
     * default constructor
     */
    public Player() {
        score = 0;
        name = "";
        avatar = "avatar7";
        chessSkin = "default";
        color1 = defaultColor1;
        color2 = defaultColor2;
        background = "apple";
    }

    /**
     * turn a string into player, any invalid parameter
     * will cause exception
     */
    public Player(String playerMessage) {
        String[] data = playerMessage.split(" ");
        System.out.println(data.length);
        if (data.length != 7) {
            throw new ConstructorException("Invalid message number");
        }
        setScore(Integer.parseInt(data[0]));
        setName(data[1]);
        setAvatar(data[2]);
        setChessSkin(data[3]);
        setColor1(Color.valueOf(data[4]));
        setColor2(Color.valueOf(data[5]));
        setBackground(data[6]);
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

    public void setAvatar(String avatar) {
        if (avatar == null)
            throw new IllegalArgumentException("avatar can't be null!");
        this.avatar = avatar;
    }

    public void setChessSkin(String chessSkin) {
        if (chessSkin == null)
            throw new IllegalArgumentException("chessSkin can't be null!");
        this.chessSkin = chessSkin;
    }

    public void setColor1(Color color){
        this.color1 = color;
    }

    public void setColor2(Color color){
        this.color2 = color;
    }

    public void setBackground(String background) {
        if (chessSkin == null)
            throw new IllegalArgumentException("background can't be null!");
        this.background = background;
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

    public Color getColor1(){
        return this.color1;
    }

    public Color getColor2(){
        return this.color2;
    }

    public String getBackground() {
        return background;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(score).append(" ");
        sb.append(name).append(" ");
        sb.append(avatar).append(" ");
        sb.append(chessSkin).append(" ");
        sb.append(color1.toString()).append(" ");
        sb.append(color2.toString()).append(" ");
        sb.append(background);
        return sb.toString();
    }
}
