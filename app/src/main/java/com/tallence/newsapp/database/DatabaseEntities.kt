package com.tallence.newsapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tallence.newsapp.overview.model.Article
import com.tallence.newsapp.overview.model.Source


@Entity
data class DatabaseArticle constructor(
    @PrimaryKey val url: String,
    val author: String?,
    val content: String,
    val description: String,
    val publishedAt: String?,
    val sourceId: String?,
    val sourceName: String?,
    val title: String?,
    val urlToImage: String?
)

@Entity
data class PagingInfo constructor(
    @PrimaryKey val pagingInfoKey: String,
    val pagingInfoIntValue: Int,
    val pagingInfoStringValue: String
)

fun List<DatabaseArticle>.asDomainModel(): List<Article> {
    return map {
        Article (
            url = it.url,
            title = it.title ?: "",
            description = it.description,
            author = it.author ?: "",
            content = it.content,
            publishedAt = it.publishedAt ?: "",
            source = Source(
                id =  it.sourceId ?: "",
                name = it.sourceName ?: ""
            ),
            urlToImage = it.urlToImage ?: "")
    }
}

fun List<Article>.asDatabaseModel(): Array<DatabaseArticle> {
    return map {
        DatabaseArticle (
            url = it.url,
            title = it.title ?: "",
            description = it.description,
            author = it.author ?: "",
            content = it.content,
            publishedAt = it.publishedAt ?: "",
            sourceId = it.source.id ?: "",
            sourceName = it.source.name ?: "",
            urlToImage = it.urlToImage ?: "")
    }.toTypedArray()
}