package com.ds.news.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ds.news.R
import com.ds.news.databinding.RowNewsArticleBinding

import com.ds.news.utils.inflate
import com.ds.news.storage.entity.NewsArticleDb
import com.ds.news.ui.model.NewsAdapterEvent


class NewsArticlesAdapter(
        private val listener: (NewsAdapterEvent) -> Unit
) : ListAdapter<NewsArticleDb, NewsArticlesAdapter.NewsHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsHolder(parent.inflate(R.layout.row_news_article))

    override fun onBindViewHolder(newsHolder: NewsHolder, position: Int) = newsHolder.bind(getItem(position), listener)

    class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = RowNewsArticleBinding.bind(itemView)

        fun bind(newsArticle: NewsArticleDb, listener: (NewsAdapterEvent) -> Unit) = with(itemView) {
            binding.newsTitle.text = newsArticle.title
            binding.newsAuthor.text = newsArticle.author
            binding.newsPublishedAt.text = newsArticle.publishedAt
            binding.newsImage.load(newsArticle.urlToImage) {
                placeholder(R.drawable.tools_placeholder)
                error(R.drawable.tools_placeholder)
            }
            setOnClickListener { listener(NewsAdapterEvent.ClickEvent) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsArticleDb>() {
            override fun areItemsTheSame(oldItem: NewsArticleDb, newItem: NewsArticleDb): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: NewsArticleDb, newItem: NewsArticleDb): Boolean = oldItem == newItem
        }
    }
}