package com.example.newsapp.domain.repository

import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.data.remote.model.NewsResponse
import com.example.newsapp.utils.Resource

interface RemoteRepository {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Resource<NewsResponse>

    suspend fun searchForNews(searchQuery: String, pageNumber: Int): Resource<NewsResponse>
}