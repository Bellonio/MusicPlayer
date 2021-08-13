package com.bellone.musicplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsManager {
    public static final String PATH_SETTINGS = "settings.txt";

    private final String path;

    public SettingsManager(String path) {
        this.path = path;

        createSettingsFile();
    }

    private void createSettingsFile(){
        File file = new File(path+"/"+PATH_SETTINGS);

        if(!file.exists()){
            try {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write("true\nnull\ntrue");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveMusicDirPath(String musicPath){
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lastOrder = br.readLine(), lastMusicInside;
            br.readLine();
            lastMusicInside = br.readLine();
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write( (lastOrder+"\n"+musicPath+"\n"+lastMusicInside) );
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMusicOrder(boolean random){
        String music_path = getMusicPath();
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lastMusicInside;
            br.readLine();
            br.readLine();
            lastMusicInside = br.readLine();
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write( (random+"\n"+music_path+"\n"+lastMusicInside) );
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMusicInsideDir(boolean musicInside){
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lastSetting = br.readLine()+"\n";
            lastSetting += br.readLine();
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write( (lastSetting+"\n"+musicInside) );
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMusicPath(){
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            //(prendo la seconda riga)
            String music_path = br.readLine();
            br.close();

            if(music_path.equals("null")){
                return null;
            }else{
                return music_path;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getOrder() {
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String order = br.readLine();
            br.close();

            return order.equals("true");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getMusicInsideDir() {
        File file = new File(path+"/"+PATH_SETTINGS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String musicInside;
            br.readLine();
            br.readLine();
            musicInside = br.readLine();
            br.close();

            return musicInside.equals("true");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
