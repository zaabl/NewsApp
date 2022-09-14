package com.example.newsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.remote.model.Article
import com.example.newsapp.data.remote.model.NewsResponse
import com.example.newsapp.domain.repository.RemoteRepository
import com.example.newsapp.utils.DispatcherProvider
import com.example.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel(){
    sealed class NewsEvent{
        class Success(val resultText: String, val newsResponse: NewsResponse): NewsEvent()
        class Failure(val errorText: String): NewsEvent()
        object Loading: NewsEvent()
        object Empty: NewsEvent()
    }

    var searchNewsResponse: NewsResponse? = null

    private val _conversion = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val conversion: StateFlow<NewsEvent> = _conversion
    var searchNewsPage = 1
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null

    fun getSearchNews(searchQuery: String) {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = NewsEvent.Loading
            newSearchQuery = searchQuery
            when(val response = remoteRepository.searchForNews(searchQuery, searchNewsPage)){
                is Resource.Error -> _conversion.value = NewsEvent.Failure(response.message!!)
                is Resource.Success -> {
//                    searchNewsPage++
//                    if(searchNewsResponse == null){
//                        searchNewsResponse = response.data
//                    } else{
//                        val oldArticles = searchNewsResponse?.articles
//                        val newArticles = response.data?.articles
//                        oldArticles?.addAll(newArticles!!)
//                    }
//                    _conversion.value = response.data?.let { NewsEvent.Success("Success", it) }!!
                    response.data?.let { resultResponse ->
                        if(searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                            searchNewsPage = 1
                            oldSearchQuery = newSearchQuery
                            searchNewsResponse = resultResponse
                            Log.d("debug", searchNewsResponse!!.totalResults.toString())
                        } else {
                            searchNewsPage++
                            val oldArticles = searchNewsResponse?.articles
                            val newArticles = resultResponse.articles
                            oldArticles?.addAll(newArticles)
                        }
                        Log.d("debug", "result response" + resultResponse.totalResults.toString())
                        _conversion.value = response.data.let { NewsEvent.Success("Success", searchNewsResponse ?: resultResponse) }!!
                    }
                }
            }
        }
    }
}