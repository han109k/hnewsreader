package com.example.hnnewsreader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mTextNames;
    private OnNewsListener onNewsListener;

    public RecyclerViewAdapter(ArrayList<String> mTextNames, OnNewsListener onNewsListener) {
        this.mTextNames = mTextNames;
        this.onNewsListener = onNewsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view, onNewsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.textName.setText((position + 1) + ". " + mTextNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mTextNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textName;
        //ConstraintLayout parentLayout;
        OnNewsListener onNewsListener;

        public ViewHolder(@NonNull View itemView, OnNewsListener onNewsListener) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            //parentLayout = itemView.findViewById(R.id.parent_layout);
            this.onNewsListener = onNewsListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNewsListener.onNewsClick(getAdapterPosition());
        }
    }

    public interface OnNewsListener {
        void onNewsClick(int position);
    }
}
