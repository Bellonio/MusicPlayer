package com.bellone.musicplayer;

import android.media.MediaPlayer;

import java.io.IOException;

public class MusicPlayer {

    private final MainActivity padre_mainActivity;

    private final MusicReader musicReader;
    private MediaPlayer mp;
    private int current_pos;

    public MusicPlayer(MainActivity padre_mainActivity, MusicReader musicReader, boolean shuffle) {
        this.padre_mainActivity = padre_mainActivity;
        this.musicReader = musicReader;
        this.current_pos = 0;
        this.mp = null;
    }

    public boolean musicIsPlaying(){
        if(mp != null){ return mp.isPlaying();
        }else{ return false; }
    }

    public void stopMusic(){
        if(mp != null && mp.isPlaying()){
            mp.stop();
            padre_mainActivity.restartChr();
        }
    }

    public void play(){
        if(mp == null){ play_music();
        }else{
            if(!musicIsPlaying()){
                mp.start();
                padre_mainActivity.startChr();
            }
        }
    }
    public void pause(){
        if(mp != null){
            if(musicIsPlaying()){
                mp.pause();
                padre_mainActivity.stopChr();
            }
        }
    }

    private void play_music(){
        if(musicReader != null && (musicReader.getMusics_path()).size() > 0){
            padre_mainActivity.show_seekbar();

            padre_mainActivity.restartChr();
            stopMusic();

            mp = new MediaPlayer();
            try {
                mp.setDataSource( musicReader.getMusics_path().get(current_pos) );
                mp.prepare();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        change_music(false);
                    }
                });

                padre_mainActivity.update_musicList_names(current_pos);

                String seconds = String.valueOf((mp.getDuration()/1000) %60);
                if(Integer.parseInt(seconds) < 10){ seconds = "0"+seconds; }
                String duration = ((mp.getDuration()/1000/60)
                        +padre_mainActivity.getString(R.string.durationDivider)
                        +seconds);
                padre_mainActivity.setTmpTotale(duration);

                padre_mainActivity.resetSeekBar(mp.getDuration());

                padre_mainActivity.startChr();

                mp.start();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void change_music(boolean toPrevious){
        if(musicReader != null && (musicReader.getMusics_path()).size() > 0){
            if(toPrevious){
                if(current_pos > 0){
                    current_pos--;
                }
            }else{
                if(current_pos+1 < (musicReader.getMusics_path()).size()){
                    current_pos++;
                }else{
                    current_pos = 0;
                }
            }
            play_music();
        }
    }

    public void go_to_music(int pos){
        current_pos = pos;

        stopMusic();
        play_music();
    }

    public void seekTo(int progress){
        mp.seekTo(progress);
    }
}
