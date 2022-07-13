package com.tallence.newsapp.overview.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tallence.newsapp.database.PagingInfo
import com.tallence.newsapp.database.asDatabaseModel
import com.tallence.newsapp.database.asDomainModel
import com.tallence.newsapp.database.getDatabase
import com.tallence.newsapp.network.NewsApi
import com.tallence.newsapp.utils.Constants

class NewsRepository(val context: Context) {

    private val searchTermKey = "SEARCHTERMKEY"
    private val currentPageKey = "CURRENTPAGEKEY"
    private val currentTotalResultsKey = "CURRENTTOTALRESULTSKEY"
    private var searchTermDefault = Constants.NewsApi.FIRST_SEARCH_TERM
    private var currentPageDefault = 1
    private var currentTotalResultsDefault = 0

    private val database = getDatabase(context)

    private val _news = MutableLiveData<List<Article>>()

    val news: LiveData<List<Article>>
        get() = _news

    suspend fun getSearchTerm(): String {
        val dao = database.pagingInfoDao()
        val keys = arrayListOf(searchTermKey)
        val values = dao.loadByKey(pagingInfoKeys = keys)
        return if(values.isNotEmpty() && values[0].pagingInfoStringValue.isNotBlank()) {
            values[0].pagingInfoStringValue
        } else {
            searchTermDefault
        }
    }

    suspend fun setSearchTerm(searchTerm: String) {
        val dao = database.pagingInfoDao()
        val pagingInfo = PagingInfo(searchTermKey, -1, searchTerm)
        dao.insertAll(pagingInfo)
    }

    suspend fun getCurrentPage(): Int {
        val dao = database.pagingInfoDao()
        val keys = arrayListOf(currentPageKey)
        val values = dao.loadByKey(pagingInfoKeys = keys)
        return if(values.isNotEmpty() && (values[0].pagingInfoIntValue >= 1)) {
            values[0].pagingInfoIntValue
        } else {
            currentPageDefault
        }
    }

    private suspend fun setCurrentPage(currentPage: Int) {
        val dao = database.pagingInfoDao()
        val pagingInfo = PagingInfo(currentPageKey, currentPage, "")
        dao.insertAll(pagingInfo)
    }

    suspend fun getCurrentTotalResults(): Int {
        val dao = database.pagingInfoDao()
        val keys = arrayListOf(currentTotalResultsKey)
        val values = dao.loadByKey(pagingInfoKeys = keys)
        return if(values.isNotEmpty() && (values[0].pagingInfoIntValue >= 0)) {
            values[0].pagingInfoIntValue
        } else {
            currentTotalResultsDefault
        }
    }

    private suspend fun setCurrentTotalResults(currentTotalResults: Int) {
        val dao = database.pagingInfoDao()
        val pagingInfo = PagingInfo(currentTotalResultsKey, currentTotalResults, "")
        dao.insertAll(pagingInfo)
    }

    suspend fun setDefaultPage() {
        setCurrentPage(currentPageDefault)
    }

    suspend fun increaseCurrentPage() {
        var currentPage = getCurrentPage()
        currentPage++
        setCurrentPage(currentPage)
    }

    suspend fun resetNewsList() {
        _news.value = mutableListOf()
        val dao = database.articleDao()
        dao.deleteAll()
    }

    suspend fun loadNewsOverview(query: String, pageSize: Int = Constants.NewsApi.PAGE_SIZE, page: Int = Constants.NewsApi.DEFAULT_PAGE, loadFromDb: Boolean = true) {

        val dao = database.articleDao()
        var allNews: List<Article> = listOf()

        if(loadFromDb) {
            // get existing news list from database
            allNews = dao.getAll().asDomainModel()
            _news.value = allNews
        }

        // get news list from api
        val apiResponse = NewsApi.retrofitService.getNewsOverviewAsync(q=query, pageSize=pageSize, page=page).await()
        apiResponse.body()?.let {
            // Set paging parameters
            val apiNewsList = it.articles
            setCurrentTotalResults(it.totalResults)
            setSearchTerm(query)
            setCurrentPage(page)

            if(page == 1) {
                // delete old database entries
                dao.deleteAll()
                // store news entries in database
                dao.insertAll(*apiNewsList.asDatabaseModel())
                // set full news list
                allNews = apiNewsList
                // update live data variable
                _news.value = allNews
            }
            else {
                // store news entries in database
                dao.insertAll(*apiNewsList.asDatabaseModel())
                // set full news list
                allNews = _news.value?.plus(apiNewsList) ?: listOf()
                // update live data variable
                _news.value = _news.value?.plus(apiNewsList) ?: listOf()
            }
        }

    }

}