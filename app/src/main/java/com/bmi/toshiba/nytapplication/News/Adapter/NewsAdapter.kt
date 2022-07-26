package com.bmi.toshiba.nytapplication.News.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmi.toshiba.nytapplication.News.Results
import com.bmi.toshiba.nytapplication.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.news_item.view.*

class NewsAdapter(
    private val news : List<Results>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    class NewsViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val IMAGE_BASE = "https://https://static01.nyt.com/images/"
        fun bindMovie(news : Results){
            itemView.news_title.text = news.title
            itemView.news_details.text = news.abstract
            itemView.news_release_date.text = news.updated
            Glide.with(itemView).load(IMAGE_BASE + news.url).into(itemView.news_poster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        )
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindMovie(news.get(position))
    }
}