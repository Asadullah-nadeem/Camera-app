package com.example.cameraapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cameraapp.Fragments.LocalFragment;
import com.example.cameraapp.MainActivity;
import com.example.cameraapp.R;
import com.example.cameraapp.utils.LocalResponse;

import java.util.ArrayList;

public class LocalDataBaseAdapter extends RecyclerView.Adapter<LocalDataBaseAdapter.MyViewHolder> {
    Context context;
    ArrayList<LocalResponse> singleRowArrayList;
    SQLiteDatabase db;
    DataBaseHandler myDatabase;

    public LocalDataBaseAdapter(Context context, ArrayList<LocalResponse> singleRowArrayList, SQLiteDatabase db, DataBaseHandler myDatabase) {
        this.context = context;
        this.singleRowArrayList = singleRowArrayList;
        this.db = db;
        this.myDatabase = myDatabase;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_database_items, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.newsImage.setImageBitmap(getBitmapFromEncodedString(singleRowArrayList.get(i).image));
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();  // Get the current position dynamically
                if (position != RecyclerView.NO_POSITION) {  // Check if position is valid
                    deleteData(position, singleRowArrayList);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return singleRowArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage, delete;
        TextView id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteData(final int position, final ArrayList<LocalResponse> singleRowArrayList) {
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.defaultimage)
                .setTitle("Delete result")
                .setMessage("Are you sure you want to delete this result?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDatabase.deleteEntry(singleRowArrayList.get(position).getUid());
                        singleRowArrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        myDatabase.close();
                        ((MainActivity) context).loadFragment(new LocalFragment(), true);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private Bitmap getBitmapFromEncodedString(String encodedString) {
        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(arr, 0, arr.length);
    }
}
