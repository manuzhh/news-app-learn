package com.tallence.newsapp.database

import androidx.room.*

@Dao
interface ArticleDao {
    @Query("SELECT * FROM databasearticle")
    suspend fun getAll(): List<DatabaseArticle>

    @Query("SELECT * FROM databasearticle WHERE url IN (:articleUrls)")
    suspend fun loadByUrls(articleUrls: ArrayList<String>): List<DatabaseArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg articles: DatabaseArticle)

    @Delete
    suspend fun delete(article: DatabaseArticle)

    @Query("DELETE FROM databasearticle")
    suspend fun deleteAll()
}

@Dao
interface PagingInfoDao {
    @Query("SELECT * FROM paginginfo")
    suspend fun getAll(): List<PagingInfo>

    @Query("SELECT * FROM paginginfo WHERE pagingInfoKey IN (:pagingInfoKeys)")
    suspend fun loadByKey(pagingInfoKeys: ArrayList<String>): List<PagingInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg pagingInfo: PagingInfo)

    @Delete
    suspend fun delete(pagingInfo: PagingInfo)

    @Query("DELETE FROM paginginfo")
    suspend fun deleteAll()
}