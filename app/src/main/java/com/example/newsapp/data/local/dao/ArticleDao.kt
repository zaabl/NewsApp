package com.example.newsapp.data.local.dao

import androidx.room.*
import com.example.newsapp.data.local.entity.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article) : Long

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List <Article>

    @Delete
    suspend fun deleteArticle(article: Article)
}