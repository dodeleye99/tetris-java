package com.yahoo.dodeleye;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yahoo.dodeleye.gamelib.audio.*;
import com.yahoo.dodeleye.tetris.Tetris1;

import javax.sound.sampled.*;

/**
 * Class for managing audio for the game.
 * @since 1.2.0
 */
public class GameAudio {

    public static final String MOVE_SFX_FILENAME      = "dummy-sound.wav";
    public static final String ROTATE_SFX_FILENAME    = "dummy-sound.wav";
    public static final String CLEAR_SFX_FILENAME     = "dummy-sound.wav";
    public static final String LAND_SFX_FILENAME      = "dummy-sound.wav";
    public static final String FAST_LAND_SFX_FILENAME = "dummy-sound.wav";
    public static final String LEVEL_UP_SFX_FILENAME  = "dummy-sound.wav";
    public static final String TETRIS_SFX_FILENAME    = "dummy-sound.wav";
    public static final String GAME_OVER_SFX_FILENAME = "dummy-sound.wav";

    public static final String BG_MUSIC_FILENAME      = "dummy-sound.wav";

    public static final AudioClip MOVE_CLIP      = createAudioClip(MOVE_SFX_FILENAME);
    public static final AudioClip ROTATE_CLIP    = createAudioClip(ROTATE_SFX_FILENAME);
    public static final AudioClip CLEAR_CLIP     = createAudioClip(CLEAR_SFX_FILENAME);
    public static final AudioClip LAND_CLIP      = createAudioClip(LAND_SFX_FILENAME);
    public static final AudioClip FAST_LAND_CLIP = createAudioClip(FAST_LAND_SFX_FILENAME);
    public static final AudioClip LEVEL_UP_CLIP  = createAudioClip(LEVEL_UP_SFX_FILENAME);
    public static final AudioClip TETRIS_CLIP    = createAudioClip(TETRIS_SFX_FILENAME);
    public static final AudioClip GAME_OVER_CLIP = createAudioClip(GAME_OVER_SFX_FILENAME);

    public static final AudioClip BG_MUSIC_CLIP = createAudioClip(BG_MUSIC_FILENAME, -10);

    private static InputStream getSoundResource(String filePath){
        return GameAudio.class.getResourceAsStream("/sound/" + filePath);
    }

    private static AudioClip createAudioClip(String soundFileName, float dv){


        InputStream is = new BufferedInputStream(getSoundResource(soundFileName));
        return new AudioClip(is, dv);

    }

    private static AudioClip createAudioClip(String soundFilePath){

        return createAudioClip(soundFilePath, 0);
    }

    private static void playSound(String soundFileName, boolean loop){

        InputStream is = new BufferedInputStream(getSoundResource(soundFileName));
        AudioManager.playAudioClip(is, loop);
    }

    public static void playSound(String soundFileName){

        playSound(soundFileName, false);
    }

    public static void changeVolume(Clip clip, float dv){

        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(dv);
    }


}
