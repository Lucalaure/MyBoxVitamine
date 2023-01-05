package com.example.vitamin_app;
import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>{

    private ArrayList<String> vitamin_names = new ArrayList<>();
    ArrayList<String> vitamin_descriptions = new ArrayList<>();
    ArrayList<String> vitamin_dosages = new ArrayList<>();
    ArrayList<Integer> vitamin_img = new ArrayList<>();
    Context context;

    public recyclerAdapter(Context aContext, String page_type) throws IllegalAccessException {
        context = aContext;

        ArrayList<String[]> str = GeneralListActivity.getDatabaselist();
        for(int i = 0; i < str.size(); i++){
            if(str.get(i)[1].equals(page_type)){
                vitamin_names.add(str.get(i)[0]);
                vitamin_descriptions.add(str.get(i)[2]);
                vitamin_dosages.add(str.get(i)[3]);
                int resourceId = context.getResources().getIdentifier(str.get(i)[4], "drawable", context.getPackageName());//initialize res and context in adapter's contructor
                vitamin_img.add(resourceId);
            }
        }
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgVitamin;
        public TextView vitaminDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imgVitamin = (ImageView) itemView.findViewById(R.id.imgVitamin);
            this.vitaminDesc = (TextView) itemView.findViewById(R.id.vitaminDesc);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem= layoutInflater.inflate(R.layout.energy_list_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.vitaminDesc.setText(vitamin_descriptions.get(position));
        holder.imgVitamin.setImageResource(vitamin_img.get(position).intValue());
    }

    @Override
    public int getItemCount() {
        return vitamin_names.size();
    }

}
