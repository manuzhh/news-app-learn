package com.tallence.newsapp.network.interfaces

import com.tallence.newsapp.overview.model.NewsOverviewResponse
import com.tallence.newsapp.utils.Constants
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("/v2/everything")
    fun getNewsOverviewAsync(
        @Query("apiKey") apiKey: String? = Constants.NewsApi.API_KEY,
        @Query("q") q: String,
        @Query("pageSize") pageSize: Int = 100,
        @Query("page") page: Int = 1
    ):
            Deferred<Response<NewsOverviewResponse>>
}