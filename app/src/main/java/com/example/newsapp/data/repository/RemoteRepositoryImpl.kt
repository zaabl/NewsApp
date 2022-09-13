package com.example.newsapp.data.repository

import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.data.remote.NewsAPI
import com.example.newsapp.data.remote.model.NewsResponse
import com.example.newsapp.domain.repository.RemoteRepository
import com.example.newsapp.utils.Resource
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor (
    private val newsAPI: NewsAPI
    ) : RemoteRepository {
    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Resource<NewsResponse> {
        return try{
            val response = newsAPI.getBreakingNews(countryCode, pageNumber)
            val result = response.body()
            if(response.isSuccessful && result != null){
                Resource.Success(result)
            }
            else
                Resource.Error(response.message())
        } catch (e: Exception){
            Resource.Error(e.message ?: "An error occurred!")
        }
    }

    override suspend fun searchForNews(
        searchQuery: String,
        pageNumber: Int
    ): Resource<NewsResponse> {
        return try{
            val response = newsAPI.searchForNews(searchQuery, pageNumber)
            val result = response.body()
            if(response.isSuccessful && result != null){
                Resource.Success(result)
            }
            else
                Resource.Error(response.message())
        } catch (e: Exception){
            Resource.Error(e.message?: "An error occurred!")
        }
    }
}