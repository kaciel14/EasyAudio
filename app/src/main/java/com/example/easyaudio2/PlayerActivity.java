package com.example.easyaudio2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerActivity extends AppCompatActivity {

    MediaPlayer player;
    Uri uri;
    ImageButton playButton, pauseButton, stopButton;
    TextView textTitulo;
    SeekBar seekBar;
    String audio, titulo;
    int pos = 0;
    Handler handler = new Handler();

    ArrayList<String> comandos = new ArrayList<>(Arrays.asList("siguiente", "atrás", "pausar", "reproducir", "anterior"));
    String[] siguiente;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            ArrayList<String> data = result.getData() != null ? result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) : null;
            boolean b = false;
            if (data != null){
                String comando = data.get(0);
                Bundle bundle = new Bundle();

                if (comando.contains("siguiente")){
                    if (pos == 0){
                        Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                        bundle.putString("url", siguiente[0]);
                        bundle.putString("titulo", siguiente[1]);
                        intent.putExtras(bundle);
                        if (siguiente!= null && siguiente.length == 2){
                            uri = Uri.parse(siguiente[0]);
                            player = MediaPlayer.create(this,uri);
                            seekBar.setMax(player.getDuration());
                            textTitulo.setText(siguiente[1]);
                        }
                        pos = 1;
                    }

                } else if (comando.contains("atrás")) {
                    onBackPressed();
                } else if (comando.contains("pausar")) {
                    pause();
                    b = true;
                } else if (comando.contains("reproducir")) {
                    play();
                }else if (comando.contains("anterior")){
                    if (pos == 1){
                        uri = Uri.parse(audio);
                        player = MediaPlayer.create(this,uri);
                        seekBar.setMax(player.getDuration());
                        textTitulo.setText(titulo);
                    }
                    pos = 0;

                }
            }
            if (b){

            }else{
                play();
            }
        }
        if (result.getResultCode() == RESULT_CANCELED && player != null){
            play();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        audio = bundle.getString("url");
        titulo = bundle.getString("titulo");
        siguiente = bundle.getStringArray("siguiente");

        BottomNavigationView navigationView1 = findViewById(R.id.bottomNav);
        View voice = navigationView1.findViewById(R.id.voiceButton);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        textTitulo = findViewById(R.id.titulo);
        textTitulo.setText(titulo);

        seekBar = findViewById(R.id.seekbar);


        uri = Uri.parse(audio);

        player = MediaPlayer.create(this,uri);

        seekBar.setMax(player.getDuration());

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });


        player.start();

        UpdateSeekBar updateSeekBar = new UpdateSeekBar();
        handler.post(updateSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showTooltip(view, "COMANDOS:\n\nATRÁS: Retrocede a la pantalla anterior\nPAUSAR: Pausa el reproductor\nREPRODUCIR: Vuelve a reproducir el audio\nSIGUIENTE: Reproduce la pista siguiente\nANTERIOR: Reproduce la pista anterior");
                return false;
            }
        });

        navigationView1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (player != null && player.isPlaying()){
                    pause();
                }
                escucharVoz();
                return false;
            }
        });
    }

    public void play(){
        if(player == null){
            player = MediaPlayer.create(this,uri);

            seekBar.setMax(player.getDuration());
            player.start();
        }else{
            player.start();
        }
    }

    public void pause(){
            player.pause();
    }

    public void stop(){
        if (player != null){
            player.stop();
            player = null;
        }else{

        }

    }

    public class UpdateSeekBar implements Runnable{

        @Override
        public void run() {
            if(player != null){
                seekBar.setProgress(player.getCurrentPosition());
            }else{
                seekBar.setProgress(0);
            }

            handler.postDelayed(this, 100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    public void escucharVoz(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        activityResultLauncher.launch(intent);
    }

    private void showTooltip(View view, String text) {
        // Usa TooltipCompat para mostrar el texto flotante
        CustomTooltip customTooltip = new CustomTooltip(this, view, text);
        customTooltip.show();
    }
}