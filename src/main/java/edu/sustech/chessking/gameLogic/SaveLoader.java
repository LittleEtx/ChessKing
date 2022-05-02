package edu.sustech.chessking.gameLogic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SaveLoader {
    private static File savePath = new File("saves");
    private static File playerPath = new File("player");

    /**
     * @param player read which player's saves
     * @return all available saves of a player, in order of time
     */
    public static ArrayList<Save> readSaveList(Player player) {
        ArrayList<Save> saveList = new ArrayList<>();
        //check root path exist
        if (!savePath.exists()) {
            return saveList;
        }

        //check player dictionary exist
        File[] allPlayerSavePath = savePath.listFiles();
        if (allPlayerSavePath == null)
            return saveList;
        File playerSavePath = null;
        for (File path : allPlayerSavePath) {
            if (path.isDirectory() &&
                    path.getName().equals(player.getName()))
                playerSavePath = path;
        }
        if (playerSavePath == null)
            return saveList;

        //read all the saves
        File[] allSaves = playerSavePath.listFiles();
        if (allSaves == null) {
            throw new RuntimeException("allSaves should be a dictionary");
        }
        Save save;
        for (File saveFile : allSaves) {
            if (!saveFile.isFile())
                continue;

            if ((save = readSave(saveFile.toPath())) != null)
                saveList.add(save);
        }

        return saveList;
    }

    /**
     * @param path the stream string of the save file
     * @return null when the save is not correct
     */
    private static Save readSave(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            long uuid = Long.parseLong(lines.get(0));
            Player whitePlayer = new Player();



        }
        catch (Exception e) {
            return null;
        }


        return null;
    }

    public static boolean addSave(Save save) {
        return false;
    }


    public static ArrayList<Player> readPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();
        return playerList;
    }

    public static boolean savePlayerList(ArrayList<Player> playerList) {
        return false;
    }

}
