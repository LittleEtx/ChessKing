package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SaveLoader {
    private static final File localSavePath = new File("localSaves");
    private static final File serverSavePath = new File("serverSaves");

    private static final File playerPath = new File("player");

    /**
     * @param player read which player's saves
     * @return all available saves of a player, in order of time
     */
    public static ArrayList<Save> readLocalSaveList(Player player) {
        return getSaves(localSavePath, player.getName());
    }

    /**
     * @param player read which player's saves
     * @return all available saves of a player, in order of time
     */
    public static ArrayList<Save> readServerSaveList(Player player) {
        return getSaves(serverSavePath, player.getName());
    }


    private static ArrayList<Save> getSaves(File savePath, String identifier) {
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
                    path.getName().equals(identifier))
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
            if (!saveFile.getName().endsWith(".save"))
                continue;
            if ((save = readSave(saveFile.toPath())) != null)
                saveList.add(save);
        }

        saveList.sort(Comparator.comparing(o -> o.getSaveDate().toString()));
        return saveList;
    }

    /**
     * @param path the stream string of the save file
     * @return null when the save is not correct
     */
    private static Save readSave(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            String[] timeData = lines.get(0).split(" ");
            long uuid = Long.parseLong(timeData[0]);
            LocalDateTime saveDate = LocalDateTime.parse(timeData[1]);

            Player whitePlayer = new Player(lines.get(1));
            Player blackPlayer = new Player(lines.get(2));

            String[] gameStateData = lines.get(3).split(" ");
            ColorType defaultDownColor = ColorType.toEnum(gameStateData[0]);
            double gameTime = Double.parseDouble(gameStateData[1]);
            double turnTime = Double.parseDouble(gameStateData[2]);

            GameCore testGameCore = new GameCore();
            testGameCore.initialGame();
            Move move;
            //time limit is off
            if (gameTime < 0) {
                for (int i = 4; i < lines.size(); i++) {
                    move = new Move(lines.get(i));
                    if (!testGameCore.moveChess(move))
                        return null;
                }

                return new Save(uuid, saveDate,
                        whitePlayer, blackPlayer,
                        defaultDownColor,
                        testGameCore.getGameHistory());
            }
            //time limit is on
            else {
                ArrayList<Double> remainingTime = new ArrayList<>();
                for (int i = 4; i < lines.size(); i++) {
                    String[] moveData = lines.get(i).split(" ");
                    move = new Move(Arrays.copyOf(moveData, moveData.length - 1));
                    testGameCore.moveChess(move);
                    remainingTime.add(Double.valueOf(moveData[moveData.length - 1]));
                }

                return new Save(uuid, saveDate,
                        whitePlayer, blackPlayer,
                        defaultDownColor, gameTime, turnTime,
                        remainingTime,
                        testGameCore.getGameHistory()
                );
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * add a save to the local player's dictionary
     * @return if save success
     */
    public static boolean addLocalSave(Save save, Player player) {
        return addSave(save, Path.of(localSavePath + "\\" + player.getName()));
    }

    /**
     * add a save to the server player's dictionary
     * @return if save success
     */
    public static boolean addServerSave(Save save, Player player) {
        return addSave(save, Path.of(serverSavePath + "\\" + player.getName()));
    }

    private static boolean addSave(Save save, Path savePath) {
        //if player path do not exist, creates it
        if (!savePath.toFile().exists()) {
            if (!savePath.toFile().mkdirs())
                return false;
        }

        File file = new File(savePath.toString() + "\\" +
                save.getUuid() + ".save");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(save.getUuid() + " " + save.getSaveDate().toString() + "\n");

            writer.write(save.getWhitePlayer().toString() + "\n");
            writer.write(save.getBlackPlayer().toString() + "\n");
            writer.write(save.getDefaultDownColor().toString() + " " +
                    save.getGameTime() + " " + save.getTurnTime() + "\n");
            MoveHistory moveHistory = save.getGameHistory();
            if (save.getGameTime() < 0) {
                for (int i = 0; i < moveHistory.getMoveNum(); i++) {
                    writer.write(moveHistory.getMove(i).toString() + "\n");
                }
            }
            else {
                ArrayList<Double> timeList = save.getRemainingTime();
                if (moveHistory.getMoveNum() != timeList.size())
                    throw new RuntimeException("Move history num abd remaining time not match!");

                for (int i = 0; i < moveHistory.getMoveNum(); i++) {
                    writer.write(moveHistory.getMove(i).toString() + " " +
                            timeList.get(i).toString() + "\n");
                }
            }
            return true;
        }
        catch (Exception e) {
            if (file.exists())
                file.delete();
            return false;
        }
    }


    public static ArrayList<Player> readPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();
        return playerList;
    }

    public static boolean savePlayerList(ArrayList<Player> playerList) {
        return false;
    }

}
