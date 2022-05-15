package com.ds.news.storage

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ds.news.utils.DaoTest
import com.ds.news.utils.assertItems
import com.ds.news.storage.entity.NewsArticleDb
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsArticlesDaoTest : DaoTest<NewsDatabase>(NewsDatabase::class.java) {

    @Test
    @Throws(InterruptedException::class)
    fun insertNewsArticles() {
        // GIVEN
        val input = listOf(NewsArticleDb(1), NewsArticleDb(2))

        // THEN
        assertThat(db.newsArticlesDao().insertArticles(input), equalTo(listOf(1L, 2L)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertNewsArticlesAndRead(): Unit = runBlocking {
        // GIVEN
        val input = listOf(
                NewsArticleDb(1, "First", "Hello"),
                NewsArticleDb(2, "Second", "Testing")
        )
        db.newsArticlesDao().insertArticles(input)

        // THEN
        db.newsArticlesDao().getNewsArticles().assertItems(input)
    }
}