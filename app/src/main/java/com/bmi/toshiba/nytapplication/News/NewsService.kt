package com.bmi.toshiba.nytapplication.News

import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

interface NewsService {

    @GET("mostpopular/v2/viewed/1.json?api-key=QzBPMmqyDK9OlP5jhfgT8GnFNqKSGGFv")
    fun getNews() : Call<NewsResponse>

}