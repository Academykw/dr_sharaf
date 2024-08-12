package com.kw.sharaf;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ListView musicListView;
        private ArrayList<String> musicFiles;
        private MediaPlayer mediaPlayer;
        private SeekBar seekBar;
        private TextView current,total;
        private ImageButton next,prev;
         private Handler handler = new Handler();
        private  Runnable updateSeekBar;

        Button play;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListView = findViewById(R.id.listMusic);
        musicFiles = new ArrayList<>();
        play = findViewById(R.id.playPauseButton);
        prev = findViewById(R.id.previousButton);
        next = findViewById(R.id.nextButton);
        seekBar = findViewById(R.id.seekBar);
        current = findViewById(R.id.currentTime);
        total = findViewById(R.id.totalTime);



        //Get music from asset folder
        try {
            String[] files = getAssets().list("music");
            if (files != null) {
            for(String file: files){
                if (!file.endsWith(".mp3") || !file.endsWith(".amr")) {


                    musicFiles.add(file);

                }


            }        }}catch (IOException e){
            e.printStackTrace();
        }





        //set music to listview
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,musicFiles);

               musicListView.setAdapter(adapter);

               //set up music player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                int totalTime = mediaPlayer.getDuration();
                total.setText(formatTime(totalTime));
                updateSeekBar();

            }
        });

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getAssets().openFd("music/" + musicFiles.get(position)));
                    mediaPlayer.prepare();




                }catch (IOException e){
                    e.printStackTrace();
                }
                mediaPlayer.start();
                play.setBackground(getDrawable(R.drawable.pause));
            }
        });




        //set up play button
    play.setOnClickListener(v -> {
        if (mediaPlayer.isPlaying()){

            mediaPlayer.pause();
            play.setBackground(getDrawable(R.drawable.ply));
    }else {
            mediaPlayer.start();
            play.setBackground(getDrawable(R.drawable.pause));
        }
            }
        );







        //set up seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);

                }
                current.setText(formatTime(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks( updateSeekBar);


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar();

            }

        });

        mediaPlayer.setOnCompletionListener(mp -> {

            seekBar.setProgress(0);
            current.setText("00:00");
            updateSeekBar();
           // handler.removeCallbacks(updateSeekBar);

                }

        );




        //set up next button
        next.setOnClickListener(view ->{
            int currentPosition = musicListView.getSelectedItemPosition();
            if (currentPosition < musicFiles.size()-1){
                musicListView.setSelection(currentPosition + 1);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getAssets().openFd(  "music/" + musicFiles.get(currentPosition + 1)).getFileDescriptor());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    play.setBackground(getDrawable(R.drawable.pause));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        } );



    }
    @SuppressLint("DefaultLocale")
    private String formatTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        current.setText(formatTime(mediaPlayer.getCurrentPosition()));
        updateSeekBar = this::updateSeekBar;
        handler.postDelayed(updateSeekBar, 1000); // Update every second
    }


}