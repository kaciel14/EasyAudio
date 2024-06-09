package com.example.easyaudio2;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;


public class ListaPodcast extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LayoutInflater layoutInflater;
    Context context;

    int categoria;
    List<ListElement> elementsX;

    ArrayList<String> comandos = new ArrayList<>(Arrays.asList("iniciar", "atrás"));

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            ArrayList<String> data = result.getData() != null ? result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) : null;
            if (data != null){
                String comando = data.get(0);
                Bundle bundle = new Bundle();

                if (comando.contains("iniciar")){
                    Intent intent = new Intent(ListaPodcast.this, PlayerActivity.class);
                    bundle.putString("url", elementsX.get(0).getAudio());
                    bundle.putString("titulo", elementsX.get(0).getTitulo());
                    if (1 <= elementsX.size()){
                        bundle.putStringArray("siguiente", new String[]{elementsX.get(1).getAudio(), elementsX.get(1).getTitulo()});
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (comando.contains("atrás")) {
                    Intent intent = new Intent(ListaPodcast.this, MainActivity.class);
                    startActivity(intent);
                }
            }

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_podcast);

        BottomNavigationView navigationView1 = findViewById(R.id.bottomNav);
        View voice = navigationView1.findViewById(R.id.voiceButton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        categoria = bundle.getInt("categoria");

        layoutInflater = getLayoutInflater();
        context = layoutInflater.getContext();
        init();

        voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showTooltip(view, "COMANDOS:\n\nINICIAR: Reproduce la primer pista disponible\nATRÁS: Retrocede a la pantalla anterior");
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

    public void init(){

        db.collection("Audios").whereEqualTo("categoria", categoria)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    List<ListElement> elements = new ArrayList<>();
                    ListElement element;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListElement element2 = document.toObject(ListElement.class);
                                element = element2;
                                elements.add(element);
                            }
                            ListAdapter listAdapter = new ListAdapter(elements, context);
                            listAdapter.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url", elements.get(position).getAudio());
                                    bundle.putString("titulo", elements.get(position).getTitulo());
                                    if (position+1 <= elements.size()-1){
                                        bundle.putStringArray("siguiente", new String[]{elements.get(position+1).getAudio(), elements.get(position+1).getTitulo()});
                                    }
                                    Intent intent = new Intent(context, PlayerActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                            RecyclerView recyclerView = findViewById(R.id.rv);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setAdapter(listAdapter);
                            elementsX = elements;

                        }else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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