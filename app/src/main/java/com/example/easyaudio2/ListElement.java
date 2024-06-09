package com.example.easyaudio2;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ListElement {
    public String titulo, audio;


    public ListElement(String titulo) {

        this.titulo = titulo;
    }
    public  ListElement(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
