package com.example.vitamin_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitamin_app.Adapters.NewsAdapter;
import com.example.vitamin_app.ApiUtilites;
import com.example.vitamin_app.MainNews;
import com.example.vitamin_app.Model.NewsModel;
import com.example.vitamin_app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    // Set the filters for the kind of news that you would like to retrieve. You can filter based
    // on the country and category of  the news.
    String api="60d7ed3a1b204cc7aabdd73fa2dc124f";
    ArrayList<NewsModel> newsModelArrayList;
    NewsAdapter newsAdapter;
    String country="us";
    private RecyclerView health;
    private String category="health";

    // Create an adapter that will be used to dynamically create a list of news articles.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.healthfragment,null);


        health = view.findViewById(R.id.recyclerviewofhealth);
        newsModelArrayList = new ArrayList<>();
        health.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext(), newsModelArrayList);
        health.setAdapter(newsAdapter);

        update();
        return view ;
    }

    // Makes an Api call to newsapi.com with the specified filter
    // and the provided Api key
    private void update() {
        // Api call uses a predefined ‘Get’ statement in the ApiInterface class.
        ApiUtilites.getApiInterface().getCategoryNews(country,category,100,api).enqueue(new Callback<MainNews>() {
            @Override
            public void onResponse(Call<MainNews> call, Response<MainNews> response) {
                if(response.isSuccessful())
                {
                    newsModelArrayList.addAll(response.body().getArticles());
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MainNews> call, Throwable t) {}
        });

    }
}
