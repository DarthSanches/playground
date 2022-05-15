package com.ds.news

import com.ds.news.mapper.Mapper
import com.ds.news.api.NewsArticle
import com.ds.news.storage.entity.NewsArticleDb

interface NewsMapper : Mapper<NewsArticleDb, NewsArticle> {
    override fun NewsArticleDb.toRemote(): NewsArticle {
        return NewsArticle(
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content,
            source = NewsArticle.Source(source.id, source.name)
        )
    }

    override fun NewsArticle.toStorage(): NewsArticleDb {
        return NewsArticleDb(
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content,
            source = NewsArticleDb.Source(source.id, source.name)
        )
    }
}