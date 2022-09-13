package com.example.newsapp.domain.repository

import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.utils.Resource

interface LocalRepository {

    suspend fun insertArticle(article: Article): Long

    suspend fun getAllArticles(): List<Article>

    suspend fun deleteArticle(article: Article)
}