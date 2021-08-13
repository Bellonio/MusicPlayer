package com.bellone.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Music_Adapter extends ArrayAdapter<String> {
    private final Context context;
    private final int res;
    private final ArrayList<String> musics_name;
    public String getCurrentMusicName(){
        if(lastMusicSelected != -1 && musics_name != null) {
            return musics_name.get(lastMusicSelected);
        }else{
            return null;
        }
    }

    private int lastMusicSelected;

    public Music_Adapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.context = context;
        this.res = resource;
        this.musics_name = (ArrayList<String>) objects;

        this.lastMusicSelected = -1;
    }

    @SuppressLint({"ViewHolder", "RtlHardcoded"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(res, parent, false);

        TextView currentMusicName = convertView.findViewById(R.id.current_music_name);
        currentMusicName.setText( Html.fromHtml(musics_name.get(position)) );

        if(position == lastMusicSelected){
            currentMusicName.setTextColor(Color.RED);
            currentMusicName.setTextSize(19);
            currentMusicName.setGravity(Gravity.RIGHT);
            currentMusicName.setPadding(0, 0, 30, 0);
            currentMusicName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

        return convertView;
    }

    public int getLastMusicSelected() { return lastMusicSelected; }
    public void setLastMusicSelected(int pos){
        lastMusicSelected = pos;
    }
}
