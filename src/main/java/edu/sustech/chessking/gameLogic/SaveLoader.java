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
    private static final File localSavePath = new File("saves\\localSaves");
    private static final File serverSavePath = new File("saves\\serverSaves");

    private static final File playerPath = new File("saves\\player");

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
    public static ArrayList<Save> readServerSaveList(String serverIdentifier, Player player) {
        return getSaves(new File(serverSavePath + "\\" + serverIdentifier), player.getName());
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
     * add a save to the server player's dictionary.
     * override the save of the same uuid.
     * @return if save success
     */
    public static boolean addServerSave(String serverIdentifier,Save save, Player player) {
        return addSave(save, Path.of(serverSavePath + "\\" +
                serverIdentifier + "\\" + player.getName()));
    }

    private static boolean addSave(Save save, Path savePath) {
        //if player path do not exist, creates it
        if (!savePath.toFile().exists()) {
            if (!savePath.toFile().mkdirs())
                return false;
        }

        File file = new File(savePath + "\\" + save.getUuid() + ".save");

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

    /**
     * @return a list of all exist players
     */
    public static ArrayList<Player> readPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();
        if (!playerPath.exists())
            return playerList;

        File[] allPlayers = playerPath.listFiles();
        if (allPlayers == null)
            return playerList;

        for (File playerFile : allPlayers) {
            if (!playerFile.isFile() ||
                    playerFile.getName().endsWith(".data"))
                continue;

            try {
                playerList.add(new Player(
                        Files.readString(playerFile.toPath())));
            } catch (Exception e) {
            }
        }
        return playerList;
    }

    /**
     * save a player's information.
     * override the information of the player with the same name.
     * Note: if the player also change his name, use changeName method first
     * @return if save success
     */
    public static boolean savePlayer(Player player) {
        if (!playerPath.exists()) {
            if (!playerPath.mkdirs())
                return false;
        }

        File playerFile = new File(playerPath + "\\" + player.getName() + ".data");
        try (FileWriter writer = new FileWriter(playerFile)) {
            writer.write(player.toString());
        } catch (Exception e) {
            //failed to write the file
            if (playerFile.exists())
                playerFile.delete();
            return false;
        }
        return true;
    }

    /**
     * Use this method if a local player change his name.
     * @return if oldName can not be found or cannot change name.
     * When this happens, the old name will be kept
     */
    public static boolean changeLocalPlayerName(String oldName, String newName) {
        File playerSaveFile = new File(localSavePath + "\\" + oldName);
        //change player's file
        if (!renamePlayerFile(oldName, newName))
            return false;

        //change saveFile's name
        if (playerSaveFile.exists() && playerSaveFile.isDirectory()) {
            if (!playerSaveFile.renameTo(
                    new File(localSavePath + "\\" + newName))) {
                renamePlayerFile(newName, oldName);
                return false;
            }
        }
        return true;
    }

    private static boolean renamePlayerFile(String oldName, String newName) {
        //change player file's name
        File playerFile = new File(playerPath + "\\" +
                oldName + ".data");
        //if player do not exist
        if (!playerFile.exists() || !playerFile.isFile()) {
            return false;
        }
        Player player;
        try {
            player = new Player(Files.readString(playerFile.toPath()));
        } catch (Exception e) {
            return false;
        }

        //if change name not succeed
        if (!playerFile.renameTo(new File(playerPath + "\\" +
                newName + ".data")))
            return false;

        player.setName(newName);
        try (FileWriter writer = new FileWriter(playerFile)) {
            writer.write(player.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Use this method if a server player change his name
     * @return if change succeed
     */
    public static boolean changeServerPlayerName(String serverIdentifier, String oldName, String newName) {
        File playerSaveFile = new File(serverSavePath + "\\" +
                serverIdentifier + "\\" + oldName);
        //no save: no need to change
        if (!playerSaveFile.exists() || !playerSaveFile.isDirectory())
            return true;

        return playerSaveFile.renameTo(new File(serverSavePath + "\\" +
                serverIdentifier + "\\" + newName));
    }

    /**
     * The method will delete the player's information and its saves
     * @param playerName the player to delete
     * @return find the file and successfully delete
     */
    private static boolean deletePlayer(String playerName) {
        File playerFile = new File(playerPath + "\\" +
                playerName + ".data");

        if (!playerFile.exists() || !playerFile.isFile())
            return false;

        if (!playerFile.delete())
            return false;

        File playerSaveFile = new File(
                 localSavePath + "\\" +playerName);

        if (playerSaveFile.exists() && playerSaveFile.isDirectory())
            playerSaveFile.delete();

        return true;
    }
}
