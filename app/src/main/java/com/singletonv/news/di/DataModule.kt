package com.singletonv.news.di

import android.content.Context
import com.singletonv.news.data.local.NewsDao
import com.singletonv.news.data.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    companion object {

        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): NewsDatabase {
            return NewsDatabase.getInstance(context)
        }

        @Singleton
        @Provides
        fun provideNewsDao(
            newsDatabase: NewsDatabase
        ): NewsDao {
            return newsDatabase.newsDao()
        }
    }
}