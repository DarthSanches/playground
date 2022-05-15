package com.ds.news.api


data class NewsResponse(
    val status: String = "",
    val totalResults: Int = 0,
    val articles: List<NewsArticle> = emptyList()
)