package com.ds.news.domain

import com.ds.news.ui.ViewState
import com.ds.news.utils.httpError
import com.ds.news.NewsMapper
import com.ds.news.api.NewsResponse
import com.ds.news.api.NewsService
import com.ds.news.storage.NewsArticlesDao
import com.ds.news.storage.entity.NewsArticleDb
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface NewsRepository {

    /**
     * Gets tne cached news article from database and tries to get
     * fresh news articles from web and save into database
     * if that fails then continues showing cached data.
     */
    fun getNewsArticles(): Flow<ViewState<List<NewsArticleDb>>>

    /**
     * Gets fresh news from web.
     */
    suspend fun getNewsFromWebservice(): Response<NewsResponse>
}

@Singleton
class DefaultNewsRepository @Inject constructor(
    private val newsDao: NewsArticlesDao,
    private val newsService: NewsService
) : NewsRepository, NewsMapper {

    override fun getNewsArticles(): Flow<ViewState<List<NewsArticleDb>>> = flow {
        emit(ViewState.loading())

        val freshNews = getNewsFromWebservice()
        freshNews.body()?.articles?.toStorage()?.let(newsDao::clearAndCacheArticles)

        val cachedNews = newsDao.getNewsArticles()
        emitAll(cachedNews.map { ViewState.success(it) })
    }
        .flowOn(Dispatchers.IO)

    override suspend fun getNewsFromWebservice(): Response<NewsResponse> {
        return try {
            newsService.getTopHeadlines()
        } catch (e: Exception) {
            httpError(404)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface NewsRepositoryModule {
    /* Exposes the concrete implementation for the interface */
    @Binds
    fun it(it: DefaultNewsRepository): NewsRepository
}