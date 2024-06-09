package com.example.easyaudio2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tooltip.TooltipDrawable;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView textView;
    Context context;

    ArrayList<String> comandos = new ArrayList<>(Arrays.asList("ciencia", "historia", "noticias", "otros"));

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
       if (result.getResultCode() == Activity.RESULT_OK){
           ArrayList<String> data = result.getData() != null ? result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) : null;
           if (data != null){
               String comando = data.get(0);
               Bundle bundle = new Bundle();
               Intent intent = new Intent(MainActivity.this, ListaPodcast.class);

               if (comando.contains("ciencia")){
                   bundle.putInt("categoria", 1);
                   intent.putExtras(bundle);
                   startActivity(intent);
               } else if (comando.contains("historia")) {
                   bundle.putInt("categoria", 2);
                   intent.putExtras(bundle);
                   startActivity(intent);
               } else if (comando.contains("noticias")) {
                   bundle.putInt("categoria", 3);
                   intent.putExtras(bundle);
                   startActivity(intent);
               } else if (comando.contains("otros")) {
                   bundle.putInt("categoria", 4);
                   intent.putExtras(bundle);
                   startActivity(intent);
               }
           }

       }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView sc = findViewById(R.id.sc_card);
        CardView hs = findViewById(R.id.hs_card);
        CardView nw = findViewById(R.id.nw_card);
        CardView ot = findViewById(R.id.ot_card);

        BottomNavigationView navigationView1 = findViewById(R.id.bottomNav);
        View voice = navigationView1.findViewById(R.id.voiceButton);
        context = this.getApplicationContext();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la nueva actividad aquí
                Bundle bundle = new Bundle();
                if (view.equals(findViewById(R.id.sc_card))) {
                    bundle.putInt("categoria", 1);
                } else if (view.equals(findViewById(R.id.hs_card))) {
                    bundle.putInt("categoria", 2);
                } else if (view.equals(findViewById(R.id.nw_card))) {
                    bundle.putInt("categoria", 3);
                } else if (view.equals(findViewById(R.id.ot_card))) {
                    bundle.putInt("categoria", 4);
                }

                Intent intent = new Intent(MainActivity.this, ListaPodcast.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        sc.setOnClickListener(listener);
        hs.setOnClickListener(listener);
        nw.setOnClickListener(listener);
        ot.setOnClickListener(listener);

        voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showTooltip(view, "COMANDOS:\n\nCIENCIA: Abrir categoría de ciencia y tecnología\nHISTORIA: Abrir categoría de historia\nNOTICIAS: Abrir categoría de noticias\nOTROS: Abrir categoría de otros");
                return false;
            }
        });

        navigationView1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                escucharVoz();
                return false;
            }
        });
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