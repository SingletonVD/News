package com.singletonv.news.di

import android.content.Context
import com.singletonv.news.data.local.NewsDao
import com.singletonv.news.data.local.NewsDatabase
import com.singletonv.news.data.remote.NewsApiService
import com.singletonv.news.data.repository.NewsRepositoryImpl
import com.singletonv.news.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    companion object {

        @Singleton
        @Provides
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Singleton
        @Provides
        fun provideConverterFactory(json: Json): Converter.Factory {
            return json.asConverterFactory(
                "application/json".toMediaType()
            )
        }

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

        @Singleton
        @Provides
        fun provideApiService(
            converter: Converter.Factory
        ): NewsApiService {
            val baseUrl = "https://newsapi.org/"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .build()
            return retrofit.create<NewsApiService>()
        }
    }
}