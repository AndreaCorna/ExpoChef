package it.polimi.expogame.support;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * Created by andrea on 02/04/15.
 */
public class MusicPlayerManager {

    private MediaPlayer soundtrackPlayer;

    private static MusicPlayerManager instance;

    private MusicPlayerManager(Context context, int soundTrackId, int audioStreamType,boolean loop, float letfChannelVolume, float rightChannelVolume){
        soundtrackPlayer = MediaPlayer.create(context, soundTrackId);
        soundtrackPlayer.setAudioStreamType(audioStreamType);
        soundtrackPlayer.setLooping(loop);
        soundtrackPlayer.setVolume(letfChannelVolume,rightChannelVolume);
    }

    public static void initialize(Context context, int soundTrackId, int audioStreamType,boolean loop, float letfChannelVolume, float rightChannelVolume){
        if(instance == null){
            instance = new MusicPlayerManager(context, soundTrackId, audioStreamType, loop,  letfChannelVolume,  rightChannelVolume);
        }
    }

    public static MusicPlayerManager getInstance(){
        if(instance != null){
            return instance;
        }
        return null;
    }


    public boolean isPlaying(){
        return soundtrackPlayer.isPlaying();
    }

    public void startPlayer(){
        if(!isPlaying()){
            soundtrackPlayer.start();

        }
    }

    public void stopPlayer(){
        if(isPlaying()){
            soundtrackPlayer.stop();

        }
    }

    public void pausePlayer(){
        if(isPlaying()){
            soundtrackPlayer.pause();
        }
    }
}
