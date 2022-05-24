package edu.sustech.chessking.gameLogic.gameSave;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    //from 0.0 to 1.0
    public static final String MusicVolume = "music-volume";
    //from 0.0 to 1.0
    public static final String SoundVolume = "sound-volume";

    private final Map<String, String> map;

    public SettingsManager() {
        map = new HashMap<>();
    }

    public String get(String key) {
        //default
        switch (key) {
            case MusicVolume -> {
                if (map.containsKey(key))
                    try {
                        return String.valueOf(Double.parseDouble(map.get(key)));
                    } catch (NumberFormatException ignored){}
                return "0.4";
            }
            case SoundVolume -> {
                    if (map.containsKey(key))
                        try {
                            return String.valueOf(Double.parseDouble(map.get(key)));
                        } catch (NumberFormatException ignored){}
                    return "1.0";
            }
            default -> {
                return "Wrong Key Type!";
            }
        }
    }

    public boolean read(String data){
        String[] msg = data.split(":");
        if (msg.length < 2)
            return false;

        map.put(msg[0].trim(), msg[1].trim());
        return true;
    }

    public void put(String key, String value) {
        map.put(key.trim(), value.trim());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key).append(":").append(map.get(key)).append("\n");
        }
        return sb.toString();
    }
}
