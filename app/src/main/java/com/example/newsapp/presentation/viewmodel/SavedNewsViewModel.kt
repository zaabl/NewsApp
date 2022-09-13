package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.domain.repository.LocalRepository
import com.example.newsapp.utils.DispatcherProvider
import com.example.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel(){
    sealed class NewsEvent{
        class Success(val resultText: String, val listOfArticles: List<Article>): NewsEvent()
        class Failure(val errorText: String): NewsEvent()
        object Loading: NewsEvent()
        object Empty: NewsEvent()
    }

    private val _conversion = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val conversion: StateFlow<NewsEvent> = _conversion

    fun getSavedNews(){
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = NewsEvent.Loading
            val response = localRepository.getAllArticles()
            _conversion.value = NewsEvent.Success("Success", response)
//            when(val response = localRepository.getAllArticles()){
//                is Resource.Error -> _conversion.value = NewsEvent.Failure(response.message!!)
//                is Resource.Success -> _conversion.value =
//                    response.data?.let { NewsEvent.Success("Success", it) }!!
//            }
        }
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        localRepository.deleteArticle(article)
    }

    fun saveArticle(article: Article) = viewModelScope.launch{
        localRepository.insertArticle(article)
    }

}