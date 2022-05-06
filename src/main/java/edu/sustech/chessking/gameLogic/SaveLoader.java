package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.EndGameType;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static edu.sustech.chessking.gameLogic.enumType.EndGameType.NOT_FINISH;
import static edu.sustech.chessking.gameLogic.enumType.EndGameType.toEnum;

public class SaveLoader {
    private static final File localSavePath = new File("saves/localSaves");
    private static final File serverSavePath = new File("saves/serverSaves");

    private static final File playerPath = new File("saves/player");

    private static File getPlayerFile(String playerName) {
        return new File(playerPath.toString() + "/" +
                playerName + ".data");
    }
    private static File getExistPlayerFile(String playerName) {
        File playerFile = getPlayerFile(playerName);
        if (!playerFile.exists() || !playerFile.isFile())
            return null;
        return playerFile;
    }



    /**
     * @param player read which player's saves
     * @return all available saves of a player, in order of time
     */
    public static ArrayList<Save> readLocalSaveList(Player player) {
        return getSaves(localSavePath, player);
    }

    /**
     * @param player read which player's saves
     * @return all available saves of a player, in order of time
     */
    public static ArrayList<Save> readServerSaveList(String serverIdentifier, Player player) {
        return getSaves(new File(serverSavePath + "/" + serverIdentifier), player);
    }


    private static ArrayList<Save> getSaves(File rootSaveFile, Player player) {
        ArrayList<Save> saveList = new ArrayList<>();

        //check player dictionary exist
        File playerSaveDic = getPlayerSaveDic(rootSaveFile.toString(), player.getName());
        if (playerSaveDic == null)
            return saveList;

        //read all the saves
        File[] allSaves = playerSaveDic.listFiles();
        if (allSaves == null) {
            throw new RuntimeException("allSaves should be a dictionary");
        }

        Save save;
        for (File saveFile : allSaves) {
            if (!saveFile.isFile() ||
                    !saveFile.getName().endsWith(".save"))
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
            Save save;
            EndGameType endGameType = NOT_FINISH;
            //time limit is off
            if (gameTime < 0) {
                for (int i = 4; i < lines.size() - 1; i++) {
                    move = new Move(lines.get(i));
                    if (!testGameCore.moveChess(move))
                        return null;
                    endGameType = checkEndGame(testGameCore);
                    if (endGameType != NOT_FINISH)
                        break;
                }

                save = new Save(uuid, saveDate,
                        whitePlayer, blackPlayer,
                        defaultDownColor,
                        testGameCore.getGameHistory());
            }
            //time limit is on
            else {
                ArrayList<Double> remainingTime = new ArrayList<>();
                for (int i = 4; i < lines.size() - 1; i++) {
                    String[] moveData = lines.get(i).split(" ");
                    move = new Move(Arrays.copyOf(moveData, moveData.length - 1));
                    testGameCore.moveChess(move);
                    remainingTime.add(Double.valueOf(moveData[moveData.length - 1]));
                    endGameType = checkEndGame(testGameCore);
                    if (endGameType != NOT_FINISH)
                        break;
                }

                save = new Save(uuid, saveDate,
                        whitePlayer, blackPlayer,
                        defaultDownColor, gameTime, turnTime,
                        remainingTime,
                        testGameCore.getGameHistory()
                );
            }
            EndGameType markEndGameType = toEnum(lines.get(lines.size() - 1));
            if (endGameType != NOT_FINISH)
                return new Replay(save, endGameType);
            else if (markEndGameType != NOT_FINISH)
                return new Replay(save, markEndGameType);
            else
                return save;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static EndGameType checkEndGame(GameCore gameCore) {
        if (gameCore.hasGameEnd()) {
            if (gameCore.hasDrawn())
                return EndGameType.DRAWN;
            else if (gameCore.hasWin(ColorType.WHITE))
                return EndGameType.WHITE_WIN;
            else
                return EndGameType.BLACK_WIN;
        }
        else
            return NOT_FINISH;
    }

    /**
     * add a save to the local player's dictionary
     * @return if save success
     */
    public static boolean writeLocalSave(Player player, Save save) {
        return writeSave(new File(localSavePath + "/" +
                player.getName()), save);
    }

    /**
     * add a save to the server player's dictionary.
     * override the save of the same uuid.
     * @return if save success
     */
    public static boolean writeServerSave(String serverIdentifier, Player player, Save save) {
        return writeSave(new File(serverSavePath + "/" +
                serverIdentifier + "/" + player.getName()), save);
    }

    private static boolean writeSave(File saveDic, Save save) {
        //if player path do not exist, creates it
        if (!saveDic.exists()) {
            if (!saveDic.mkdirs())
                return false;
        }

        File file = new File(saveDic + "/" + save.getUuid() + ".save");

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
            //write game end type
            if (save instanceof Replay replay)
                writer.write(replay.getEndGameType().toString());
            else
                writer.write(NOT_FINISH.toString());
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

        Player player;
        for (File playerFile : allPlayers) {
            if (!playerFile.isFile() ||
                    !playerFile.getName().endsWith(".data"))
                continue;

            try {
                player = new Player(Files.readString(playerFile.toPath()));
            } catch (Exception e) {
                System.out.println("Fail to read " + playerFile.getName());
                continue;
            }

            playerList.add(player);
        }
        return playerList;
    }

    /**
     * save a player's information.
     * override the information of the player with the same name.
     * Note: if the player also changed his name, use changeName method first
     * @return if save success
     */
    public static boolean writePlayer(Player player) {
        if (!playerPath.exists()) {
            if (!playerPath.mkdirs())
                return false;
        }

        File playerFile = new File(playerPath + "/" + player.getName() + ".data");
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
     * Note: this method will only change the name of the file,
     * i.e. save player should be called after this method.
     * @return if player of oldName can not be found or player of
     * newName already exist
     */
    public static boolean changeLocalPlayerName(String oldName, String newName) {
        //change player's file
        File playerFile = getExistPlayerFile(oldName);
        if (playerFile == null) {
            return false;
        }
        //if change name not succeed
        if (!playerFile.renameTo(new File(playerPath + "/" +
                newName + ".data")))
            return false;

        mergeSaves(localSavePath, oldName, newName);
        return true;
    }

    /**
     * Use this method if a server player change his name
     * @return if change succeed
     */
    public static boolean changeServerPlayerName(String serverIdentifier, String oldName, String newName) {
        return mergeSaves(new File(serverSavePath + "/" +
                serverIdentifier), oldName ,newName);
    }

    private static boolean mergeSaves(File rootDic, String oldName, String newName) {
        //change saveFile's name
        File oldSaveDic = getPlayerSaveDic(rootDic.toString(), oldName);
        if (oldSaveDic == null)
            return false;
        File newSaveDic = new File(rootDic + "/" + newName);

        //if not exist, simply change name
        if (!newSaveDic.exists() || !newSaveDic.isDirectory()) {
            return oldSaveDic.renameTo(newSaveDic);
        }
        //already exist, merge all files to the new dictionary
        else {
            File[] oldSaveFiles = oldSaveDic.listFiles();
            if (oldSaveFiles == null)
                return true;
            File newSaveFile;
            for (File oldSaveFile : oldSaveFiles) {
                if (oldSaveFile.isFile()) {
                    newSaveFile = new File(newSaveDic
                            + "/" + oldSaveFile.getName());

                    mergeSave(oldSaveFile, newSaveFile);
                }
            }
            oldSaveDic.delete();
        }
        return true;
    }

    private static void mergeSave(File oldSaveFile, File newSaveFile) {
        //if name conflict, use the recent one
        if (newSaveFile.exists()) {
            Save oldSave = readSave(oldSaveFile.toPath());
            Save newSave = readSave(newSaveFile.toPath());
            if (oldSave != null) {
                if (newSave == null)
                    oldSaveFile.renameTo(newSaveFile);
                else {
                    if (oldSave.getSaveDate().toString().compareTo(
                        newSave.getSaveDate().toString()) >= 0)
                        oldSaveFile.renameTo(newSaveFile);
                }
            }
        }
        else
           oldSaveFile.renameTo(newSaveFile);
    }

    private static File getPlayerSaveDic(String rootFile, String identifier) {
        File playerSaveFile = new File(rootFile + "/" + identifier);
        //no save: no need to change
        if (!playerSaveFile.exists() || !playerSaveFile.isDirectory())
            return null;
        return playerSaveFile;
    }

    /**
     * The method will delete the player's information and its saves
     * @param player the player to delete
     * @return find the file and successfully delete
     */
    public static boolean deletePlayer(Player player) {
        File playerFile = getExistPlayerFile(player.getName());
        if (playerFile == null)
            return false;
        if (!playerFile.delete())
            return false;

        File playerSaveFile = new File(
                 localSavePath + "/" + player.getName());

        if (playerSaveFile.exists() && playerSaveFile.isDirectory())
            playerSaveFile.delete();

        return true;
    }

    public static boolean deleteLocalSave(Player player, Save save) {
        return deleteSave(localSavePath, player, save);
    }

    public static boolean deleteServerSave(String serverIdentifier, Player player, Save save) {
        return deleteSave(new File(serverSavePath + "/" +
                serverIdentifier), player, save);
    }

    private static boolean deleteSave(File rootFile, Player player,Save save) {
        File saveFile = new File(rootFile + "/" + player.getName() +
                "/" + save.getUuid() + ".save");

        if (!saveFile.exists() || !saveFile.isFile())
            return false;

        return saveFile.delete();
    }

}
