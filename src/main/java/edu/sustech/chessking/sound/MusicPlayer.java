package edu.sustech.chessking.sound;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;

public class MusicPlayer {
    private static final Music mainMenuMusic = FXGL.getAssetLoader().loadMusic("BGM1.mp3");
    private static final Music inGameMusic = FXGL.getAssetLoader().loadMusic("phoenixWrightCutBGM.mp3");

    public static void play(MusicType musicType) {
        switch (musicType) {
            case IN_GAME -> {
                FXGL.getAudioPlayer().stopMusic(mainMenuMusic);
                FXGL.getAudioPlayer().loopMusic(inGameMusic);
            }
            case MENU -> {
                FXGL.getAudioPlayer().stopMusic(inGameMusic);
                FXGL.getAudioPlayer().loopMusic(mainMenuMusic);
            }
        }
    }

}
