package com.tallence.newsapp.overview.model

data class NewsOverviewResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)