package edu.sustech.chessking.gameLogic;

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
    private String color1Str;
    private String color2Str;

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
        color1Str = defaultColor1.toString();
        color2Str = defaultColor2.toString();
        background = "apple";
    }

    /**
     * turn a string into player, any invalid parameter
     * will cause exception
     */
    public Player(String playerMessage) {
        String[] data = playerMessage.split(" ");
        //System.out.println(data.length);
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

    public void incScore(int increase) {
        score += increase;
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
        this.color1Str = color.toString();
    }

    public void setColor2(Color color){
        this.color2Str = color.toString();
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
        return Color.web(color1Str);
    }

    public Color getColor2(){
        return Color.web(color2Str);
    }

    public String getBackground() {
        return background;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return score + " " +
                name + " " +
                avatar + " " +
                chessSkin + " " +
                color1Str + " " +
                color2Str + " " +
                background;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (score != player.score) return false;
        if (!name.equals(player.name)) return false;
        if (!avatar.equals(player.avatar)) return false;
        if (!chessSkin.equals(player.chessSkin)) return false;
        if (!background.equals(player.background)) return false;
        if (!color1Str.equals(player.color1Str)) return false;
        return color2Str.equals(player.color2Str);
    }

    @Override
    public int hashCode() {
        int result = score;
        result = 31 * result + name.hashCode();
        result = 31 * result + avatar.hashCode();
        result = 31 * result + chessSkin.hashCode();
        result = 31 * result + background.hashCode();
        result = 31 * result + color1Str.hashCode();
        result = 31 * result + color2Str.hashCode();
        return result;
    }
}
