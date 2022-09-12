package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.remote.model.Article
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
        class Success(val resultText: String, val listOfArticles: MutableList<Article>): NewsEvent()
        class Failure(val errorText: String): NewsEvent()
        object Loading: NewsEvent()
        object Empty: NewsEvent()
    }

    private val _conversion = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val conversion: StateFlow<NewsEvent> = _conversion
    val searchNewsPage = 1

    fun getSearchNews(searchQuery: String) {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = NewsEvent.Loading
            when(val response = remoteRepository.searchForNews(searchQuery, searchNewsPage)){
                is Resource.Error -> _conversion.value = NewsEvent.Failure(response.message!!)
                is Resource.Success -> {
                    _conversion.value = response.data?.let {NewsEvent.Success("Success", it.articles) }!!
                }
            }
        }
    }
}