package com.ds.news.domain

import com.ds.news.ui.ViewState
import com.ds.news.utils.MockitoTest
import com.ds.news.utils.assertItems
import com.ds.news.api.NewsArticle
import com.ds.news.api.NewsResponse
import com.ds.news.api.NewsService
import com.ds.news.storage.NewsArticlesDao
import com.ds.news.storage.entity.NewsArticleDb
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.InjectMocks
import org.mockito.Mock
import retrofit2.Response

@RunWith(JUnit4::class)
class NewsRepositoryTest : MockitoTest() {

    @Mock
    lateinit var newsDao: NewsArticlesDao

    @Mock
    lateinit var newsSourceService: NewsService

    @InjectMocks
    lateinit var newsRepository: DefaultNewsRepository

    @Test
    fun `get news articles from web when there is internet`() = runBlocking {
        // GIVEN
        val fetchedArticles = listOf(
            NewsArticle(title = "Fetched 1", source = NewsArticle.Source()),
            NewsArticle(title = "Fetched 2", source = NewsArticle.Source())
        )
        val cachedArticles = listOf(
            NewsArticleDb(title = "Fetched 1", source = NewsArticleDb.Source()),
            NewsArticleDb(title = "Fetched 2", source = NewsArticleDb.Source())
        )
        val newsSource = NewsResponse(articles = fetchedArticles)
        val response = Response.success(newsSource)

        // WHEN
        whenever(newsSourceService.getTopHeadlines()) doReturn response
        whenever(newsDao.getNewsArticles()) doReturn flowOf(cachedArticles)

        // THEN
        newsRepository.getNewsArticles().assertItems(
            ViewState.loading(),
            ViewState.success(cachedArticles)
        )
    }

    @Test
    fun `get cached news articles when there is no internet`() = runBlocking {
        // GIVEN
        val cachedArticles = listOf(NewsArticleDb(title = "Cached", source = NewsArticleDb.Source()))
        val error = RuntimeException("Unable to fetch from network")

        // WHEN
        whenever(newsSourceService.getTopHeadlines()) doThrow error
        whenever(newsDao.getNewsArticles()) doReturn flowOf(cachedArticles)

        // THEN
        newsRepository.getNewsArticles().assertItems(
            ViewState.loading(),
            ViewState.success(cachedArticles)
        )
    }
}