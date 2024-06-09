package com.example.easyaudio2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private List<ListElement> mData;
    private LayoutInflater layoutInflater;
    private Context context;

    ItemClickListener listener;


    private int position;

    public ListAdapter(List<ListElement> itemList, Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = itemList;
        this.context = context;
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_element, null);
        return new ListAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount(){ return mData.size(); }

    /*public void setItems(List<ListElement> items){
        mData = items;
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title;

        private ItemClickListener itemClickListener;

        ViewHolder(View itemView ,ItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagen);
            title = itemView.findViewById(R.id.titulo);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        void bindData(final ListElement item) {
            title.setText(item.getTitulo());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                // Llamar al método onItemClick del ItemClickListener
                itemClickListener.onItemClick(position);
            }else{
                Log.d("NOOOOOOO", "NOOOOOOOOOOOOOOOOOOO");
            }
        }

    }
}
