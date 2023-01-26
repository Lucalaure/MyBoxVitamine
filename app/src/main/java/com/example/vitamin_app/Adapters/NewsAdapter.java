package com.example.vitamin_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitamin_app.Models.NewsModel;
import com.example.vitamin_app.R;
import com.example.vitamin_app.Activities.WebViewActivity;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    ArrayList<NewsModel> newsModelArrayList;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsModelArrayList) {
        this.context = context;
        this.newsModelArrayList = newsModelArrayList;
    }

    // Create a new view, which defines the UI of the list item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item,null,false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view. Update the image and text of an item. Make the list item
    // navigate to the articles website shown on the item using webview.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.imageView.setOnClickListener(v -> {
            Intent intent=new Intent(context, WebViewActivity.class);
            intent.putExtra("url", newsModelArrayList.get(position).getUrl());
            context.startActivity(intent);
        });

        holder.mheading.setText(newsModelArrayList.get(position).getTitle());
        Glide.with(context).load(newsModelArrayList.get(position).getUrlToImage()).into(holder.imageView);

    }

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        return newsModelArrayList.size();
    }

    // Provide a reference to the views for each data item.
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mheading;
        ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mheading=itemView.findViewById(R.id.title);
            imageView=itemView.findViewById(R.id.imageview);

        }
    }
}
