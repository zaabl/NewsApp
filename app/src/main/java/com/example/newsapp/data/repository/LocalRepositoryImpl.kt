package com.example.newsapp.data.repository

import com.example.newsapp.data.local.database.ArticleDatabase
import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val db: ArticleDatabase
): LocalRepository{
    override suspend fun insertArticle(article: Article) = db.getArticleDao().insertArticle(article)

    override suspend fun getAllArticles() = db.getArticleDao().getSavedArticles()

    override suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}