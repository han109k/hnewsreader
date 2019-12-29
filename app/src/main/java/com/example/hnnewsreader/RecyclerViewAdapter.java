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
    private ArrayList<String> mUrlNames;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mTextNames, ArrayList<String> mUrlNames, Context mContext) {
        this.mTextNames = mTextNames;
        this.mUrlNames = mUrlNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.textName.setText((position + 1) + ". " + mTextNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on : " + mTextNames.get(position));

                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("content", mUrlNames.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTextNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
