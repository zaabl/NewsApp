package com.example.newsapp.data.local.database

import android.content.Context
import androidx.room.*
import com.example.newsapp.data.local.Converters
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.local.entity.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){
    abstract fun getArticleDao(): ArticleDao
}