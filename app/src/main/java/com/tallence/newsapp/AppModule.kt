package com.tallence.newsapp

import android.content.Context
import com.tallence.newsapp.overview.model.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("NewsRepository")
    fun provideRepo(@ApplicationContext context: Context) = NewsRepository(context)

}