package com.example.vitamin_app;

import com.example.vitamin_app.Models.NewsModel;

import java.util.ArrayList;

public class MainNews {

    private String status;
    private  String totalResult;
    private ArrayList<NewsModel> articles;

    public MainNews(String status, String totalResult, ArrayList<NewsModel> articles) {
        this.status = status;
        this.totalResult = totalResult;
        this.articles = articles;
    }

    public ArrayList<NewsModel> getArticles() {
        return articles;
    }
}
