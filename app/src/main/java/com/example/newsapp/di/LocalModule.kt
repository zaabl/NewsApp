package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.local.database.ArticleDatabase
import com.example.newsapp.data.remote.NewsAPI
import com.example.newsapp.data.repository.LocalRepositoryImpl
import com.example.newsapp.data.repository.RemoteRepositoryImpl
import com.example.newsapp.domain.repository.LocalRepository
import com.example.newsapp.domain.repository.RemoteRepository
import com.example.newsapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    fun provideArticleDao(articleDatabase: ArticleDatabase): ArticleDao {
        return articleDatabase.getArticleDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ArticleDatabase {
        return Room.databaseBuilder(
            appContext,
            ArticleDatabase::class.java,
            "article_db.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideLocalRepository(articleDatabase: ArticleDatabase): LocalRepository = LocalRepositoryImpl(articleDatabase)
}