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

    companion object{
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: createDatabase(context).also {instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}