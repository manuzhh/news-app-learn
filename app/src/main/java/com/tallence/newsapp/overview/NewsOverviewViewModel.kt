package com.tallence.newsapp.overview

import androidx.lifecycle.*
import com.tallence.newsapp.overview.model.Article
import com.tallence.newsapp.overview.model.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class NewsOverviewViewModel @Inject constructor(
    @Named("NewsRepository") val newsRepo: NewsRepository
) : ViewModel() {

    private val _navigateToSelectedArticle = MutableLiveData<Article?>()

    val navigateToSelectedArticle: MutableLiveData<Article?>
        get() = _navigateToSelectedArticle

    init {
        viewModelScope.launch {
            newsRepo.loadNewsOverview(newsRepo.getSearchTerm())
        }
    }

    fun displayArticleDetails(article: Article) {
        _navigateToSelectedArticle.value = article
    }

    fun displayArticleDetailsComplete() {
        _navigateToSelectedArticle.value = null
    }

    fun paginate() = viewModelScope.launch {
        newsRepo.increaseCurrentPage()
        newsRepo.loadNewsOverview(
            newsRepo.getSearchTerm(),
            page = newsRepo.getCurrentPage(),
            loadFromDb = false
        )
    }
}