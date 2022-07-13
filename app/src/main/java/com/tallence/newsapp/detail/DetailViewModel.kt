package com.tallence.newsapp.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tallence.newsapp.overview.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    var article: MutableLiveData<Article> = MutableLiveData<Article>()
}