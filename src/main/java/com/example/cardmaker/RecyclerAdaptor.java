package com.example.cardmaker;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdaptor extends RecyclerView.Adapter<RecyclerAdaptor.PostHolder > {

    private ArrayList<String> names;
    private ArrayList<Bitmap> pics;
    private OnNoteListener mOnNoteListener;

    public RecyclerAdaptor(ArrayList<String> names , ArrayList<Bitmap> pics , OnNoteListener onNoteListener) {
        this.names = names;
        this.pics = pics;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    public PostHolder onCreateViewHolder() {
        return onCreateViewHolder();
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_layout , parent,  false);
        return new PostHolder(view , mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.textView.setText(names.get(position));
        holder.imageView.setImageBitmap(pics.get(position));
        holder.number.setText(String.valueOf(position+1) + ")");
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView,number;
        ImageView imageView;
        OnNoteListener onNoteListener;

        public PostHolder(@NonNull View itemView , OnNoteListener onNoteListener) {
            super(itemView);

            number = itemView.findViewById(R.id.numberId);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition() , v);
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position , View view);
    }
}
