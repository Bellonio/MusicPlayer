package com.bellone.musicplayer;

import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class MusicReader {

    private final String path;

    private final ArrayList<String> musics_path1;
    private final ArrayList<String> musics_path2;

    private final ArrayList<String> musics_name;

    public ArrayList<String> getMusics_path(){
        if(shuffle){
            return musics_path2;
        }else{
            return musics_path1;
        }
    }
    public ArrayList<String> getMusicsName() { return musics_name; }

    private final boolean shuffle;
    private final boolean musicInsideDir;

    public MusicReader(String path, boolean shuffle, boolean musicInsideDir) {
        this.path = path;
        this.shuffle = shuffle;
        this.musicInsideDir = musicInsideDir;

        this.musics_path1 = new ArrayList<>();
        this.musics_path2 = new ArrayList<>();
        this.musics_name = new ArrayList<>();

        readMusic();
        onlyMusicsName();
    }

    private void readInsideDir(String path){
        File directory = new File(path);

        for(File m : directory.listFiles()){
            if(m.isDirectory()){
                readInsideDir(m.getAbsolutePath());
            }else{
                if(m.getAbsolutePath().endsWith(".mp3") || m.getAbsolutePath().endsWith(".mp4")){
                    musics_path1.add( m.getPath() );
                }
            }
        }
    }
    private void readMusic(){
        File directory = new File(path);

        for(File m : directory.listFiles()){
            if(musicInsideDir && m.isDirectory()){
                readInsideDir(m.getAbsolutePath());
            }else {
                if (m.getAbsolutePath().endsWith(".mp3") || m.getAbsolutePath().endsWith(".mp4")) {
                    musics_path1.add(m.getPath());
                }
            }
        }

        if(shuffle) {
            randomPosition();
        }else{
            orderPosition();
        }
    }

    private void orderPosition(){
        //Android 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            musics_path1.sort(new Comparator<String>() {
                @Override
                public int compare(String m1, String m2) {
                    if(m1.compareToIgnoreCase(m2) <= 0){
                        return -1;
                    }else{
                        return 1;
                    }
                }
            });
        }
    }

    private void randomPosition(){
        ArrayList<Integer> indexes_already_inserted = new ArrayList<>();

        while(musics_path2.size() != musics_path1.size()){
            //Math.random() * (max - min + 1) + min
            int randomNum = (int) (Math.random()*(musics_path1.size()));
            if( !indexes_already_inserted.contains(randomNum) ){
                musics_path2.add( musics_path1.get(randomNum) );
                indexes_already_inserted.add(randomNum);
            }
        }
    }

    private void onlyMusicsName(){
        ArrayList<String> musics;
        if(shuffle){
            musics = musics_path2;
        }else{
            musics = musics_path1;
        }

        for (String m:musics) {
            musics_name.add( m.substring(
                    m.lastIndexOf("/")+1,
                    m.lastIndexOf(".mp")
            ) );
        }
    }
}