package com.bellone.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private boolean permission;

    private Context context = null;
    private MusicReader musicReader;
    private SettingsManager settingsManager;


    private Button btnChooseDir = null;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchMusicInsideDir = null;
    private AutoCompleteTextView autoCompleteTxtMusic = null;
    private String musicDirPath = null;

    private ListView listViewMusic = null;
    private Music_Adapter music_adapter = null;

    private MusicPlayer musicPlayer;
    public boolean musicIsPlaying(){
        if(musicPlayer != null){ return musicPlayer.musicIsPlaying();
        }else{ return false; }
    }
    private ImageButton imgPrev;
    private ImageButton imgPlay;
    private ImageButton imgNext;

    private ImageButton imgShuffle;
    private ImageButton imgPause;
    private ImageButton imgOrder;


    private LinearLayout layout_seekbar;
    private Chronometer chronometer;
    private long pauseOffset;
    private SeekBar seekBar;


    public void resetSeekBar(int max){
        seekBar.setProgress(0);
        seekBar.setMax(max);
    }

    private TextView lblTmpTotale;
    public void setTmpTotale(String tmp){
        lblTmpTotale.setText(tmp);
    }

    private AppNotificationManager appNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        LinearLayout lListView = findViewById(R.id.layoutListView);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lListView.getLayoutParams();
        params.height = height / 2;
        lListView.setLayoutParams(params);

        context = this;
        permission = false;

        createAppNotificationManger();

        btnChooseDir = findViewById(R.id.btnChooseDir);
        switchMusicInsideDir = findViewById(R.id.swicthMusicsInDir);
        autoCompleteTxtMusic = findViewById(R.id.autoCompleteTxtMusic);
        listViewMusic = findViewById(R.id.listViewMusic);

        imgPrev = findViewById(R.id.imgBtnPrev);
        imgPlay = findViewById(R.id.imgBtnPlay);
        imgNext = findViewById(R.id.imgBtnNext);
        imgShuffle = findViewById(R.id.imgBtnShuffle);
        imgPause = findViewById(R.id.imgBtnPause);
        imgOrder = findViewById(R.id.imgBtnOrder);

        layout_seekbar = findViewById(R.id.layoutSeekbar);
        chronometer = findViewById(R.id.chronometer);
        seekBar = findViewById(R.id.seekBar);
        lblTmpTotale = findViewById(R.id.lblTmpTotale);

        if(ContextCompat.checkSelfPermission(context, PERMISSIONS[0]) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 10);
        }else{
            permission = true;
            createSettingsManager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            permission = true;
            createSettingsManager();
        }else{
            Snackbar.make(findViewById(android.R.id.content),
                    context.getString(R.string.noPermission),
                    Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(permission) {
            btnChooseDir.setOnClickListener(this);
            imgPrev.setOnClickListener(this);
            imgPlay.setOnClickListener(this);
            imgNext.setOnClickListener(this);
            imgShuffle.setOnClickListener(this);
            imgPause.setOnClickListener(this);
            imgOrder.setOnClickListener(this);

            switchMusicInsideDir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                    settingsManager.saveMusicInsideDir(check);
                    if(musicDirPath != null) {
                        if (musicPlayer != null) {
                            musicPlayer.stopMusic();
                        }

                        createMusicPlayer(true);
                    }
                }
            });

            /*autoCompleteTxtMusic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    autoCompleteTxtMusic.setAdapter(new ArrayAdapter<String>(context,
                            R.layout.music_searching,
                            musicReader.getMusicsFromString(editable.toString())));
                }
            });*/

            autoCompleteTxtMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int pos = musicReader.findMusicFromName(
                            String.valueOf(adapterView.getItemAtPosition(i)) );
                    autoCompleteTxtMusic.setText("");

                    musicPlayer.go_to_music(pos);
                    play();

                    hideKeyBoard();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                /*stopChr();
                pauseOffset = seekBar.getProgress();
                startChr();
                stopChr();*/
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (musicPlayer.musicIsPlaying()) {
                        stopChr();
                        pauseOffset = seekBar.getProgress();
                        startChr();

                        musicPlayer.seekTo(seekBar.getProgress());
                    }
                }
            });


            listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    musicPlayer.go_to_music(i);
                    play();
                }
            });

            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    seekBar.setProgress((int) (SystemClock.elapsedRealtime() - chronometer.getBase()));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {/*nothing*/}

    @Override
    protected void onDestroy() {
        if(musicPlayer != null) {
            musicPlayer.stopMusic();
        }
        if(appNotificationManager != null){
            appNotificationManager.cancelNotification();
        }

        super.onDestroy();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChooseDir:
                if(musicIsPlaying()){
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    resetAll_andAskMusicDir();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.yesNoDialogMessage))
                            .setPositiveButton(context.getString(R.string.positive), dialogClickListener)
                            .setNegativeButton(context.getString(R.string.negative), dialogClickListener).show();
                }else{
                    resetAll_andAskMusicDir();
                }
                break;
            case R.id.imgBtnPrev:
                previous();
                break;
            case R.id.imgBtnPlay:
                play();
                break;
            case R.id.imgBtnNext:
                next();
                break;
            case R.id.imgBtnShuffle:
                if(musicReader != null && musicReader.getMusics_path().size() > 0){
                    change_image_shuffle();
                    settingsManager.saveMusicOrder(true);
                    if(musicPlayer != null) {
                        musicPlayer.stopMusic();
                    }

                    createMusicPlayer(true);
                }
                break;
            case R.id.imgBtnPause:
                pause();
                break;
            case R.id.imgBtnOrder:
                if(musicReader != null && musicReader.getMusics_path().size() > 0) {
                    change_image_ordered();
                    settingsManager.saveMusicOrder(false);
                    if (musicPlayer != null) {
                        musicPlayer.stopMusic();
                    }

                    createMusicPlayer(false);
                }
                break;
            default:
                break;
        }
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void resetAll_andAskMusicDir(){
        if(musicPlayer != null) {
            musicPlayer.stopMusic();
        }
        stopChr();

        layout_seekbar.setVisibility(View.INVISIBLE);
        resetSeekBar(0);
        setTmpTotale(context.getString(R.string.defaultTimer));

        if(appNotificationManager != null){
            appNotificationManager.cancelNotification();
        }

        askTheMusicDir();
    }

    private void askTheMusicDir(){
        //Tutorial for the directory chooser
        //https://ourcodeworld.com/articles/read/912/how-to-create-file-folder-picker-with-a-storage-chooser-in-android-4-4-using-the-storage-chooser-library
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                musicDirPath = path;

                settingsManager.saveMusicDirPath(musicDirPath);
                createMusicPlayer(settingsManager.getOrder());
                if(settingsManager.getOrder()) {
                    change_image_shuffle();
                }else{
                    change_image_ordered();
                }
            }
        });
        chooser.show();
    }

    private void createAppNotificationManger(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appNotificationManager = new AppNotificationManager(context, this);
            appNotificationManager.cancelNotification();
        }else{
            appNotificationManager = null;
        }
    }

    private void createMusicPlayer(boolean shuffle){
        musicReader = new MusicReader(musicDirPath, shuffle, settingsManager.getMusicInsideDir());

        music_adapter = new Music_Adapter(context, R.layout.music_layout,
                musicReader.getMusicsName());
        listViewMusic.setAdapter(music_adapter);

        autoCompleteTxtMusic.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                musicReader.getMusicsName() ));

        if(musicReader.getMusics_path().size() > 0) {
            ((LinearLayout)(findViewById(R.id.layoutListView))).setVisibility(View.VISIBLE);
            ((LinearLayout)(findViewById(R.id.layoutMusicControls))).setVisibility(View.VISIBLE);

            musicPlayer = new MusicPlayer(MainActivity.this, musicReader, shuffle);

            createAppNotificationManger();
        }else{
            musicPlayer = null;
            Toast.makeText(context, context.getString(R.string.noMusicHere), Toast.LENGTH_SHORT).show();
            appNotificationManager = null;
        }
    }

    private void createSettingsManager(){
        settingsManager = new SettingsManager(getFilesDir().getPath());

        switchMusicInsideDir.setChecked(settingsManager.getMusicInsideDir());

        if(settingsManager.getMusicPath() != null){
            musicDirPath = settingsManager.getMusicPath();

            createMusicPlayer(settingsManager.getOrder());
            if(settingsManager.getOrder()) {
                change_image_shuffle();
            }else{
                change_image_ordered();
            }
        }
    }

    //--------metodi usati anche in AppNotificationManager----------------------
    public void previous(){
        if(musicPlayer != null) {
            change_image_play();
            musicPlayer.change_music(true);
        }
    }
    public void play(){
        if(musicPlayer != null) {
            change_image_play();
            musicPlayer.play();
        }
    }
    public void pause(){
        if(musicPlayer != null) {
            change_image_pause();
            musicPlayer.pause();
        }
    }
    public void next(){
        if(musicPlayer != null) {
            change_image_play();
            if(music_adapter.getLastMusicSelected() != -1) {
                musicPlayer.change_music(false);
            }else{
                play();
            }
        }
    }
    //----------------------------------------------------------------

    private void change_image_play(){
        imgPause.setImageResource(R.drawable.not_pause);
        imgPlay.setImageResource(R.drawable.play);

        if(appNotificationManager != null){
            AppNotificationManager.change_image_to_pause();
        }
    }
    private void change_image_pause(){
        imgPause.setImageResource(R.drawable.pause);
        imgPlay.setImageResource(R.drawable.not_play);

        if(appNotificationManager != null && musicIsPlaying()){
            AppNotificationManager.change_image_to_play();
        }
    }
    private void change_image_shuffle(){
        change_image_pause();
        imgShuffle.setImageResource(R.drawable.shuffle);
        imgOrder.setImageResource(R.drawable.not_ordered);
    }
    private void change_image_ordered() {
        change_image_pause();
        imgShuffle.setImageResource(R.drawable.not_shuffle);
        imgOrder.setImageResource(R.drawable.ordered);
    }


    public void show_seekbar(){
        if(layout_seekbar.getVisibility() == View.INVISIBLE){
            layout_seekbar.setVisibility(View.VISIBLE);
        }
    }

    public void update_musicList_names(int position){
        if(music_adapter != null) {
            music_adapter.setLastMusicSelected(position);
            music_adapter.notifyDataSetChanged();

            int focusOn = 0;
            if (position - 6 >= 0) {
                focusOn = position - 6;
            }
            listViewMusic.setSelection(focusOn);
            listViewMusic.requestFocus();

            if (appNotificationManager != null) {
                appNotificationManager.update_music_name(music_adapter.getCurrentMusicName());
            }
        }
    }

    //----------method to work with the chronometer-----------------
    public void startChr(){
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    public void stopChr(){
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    public void restartChr(){
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }
    //--------------------------------------------------------------
}