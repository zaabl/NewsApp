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
class BreakingNewsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class NewsEvent{
        class Success(val resultText: String, val newsResponse: NewsResponse): NewsEvent()
        class Failure(val errorText: String): NewsEvent()
        object Loading: NewsEvent()
        object Empty: NewsEvent()
    }

    var breakingNewsResponse:NewsResponse? = null

    private val _conversion = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val conversion: StateFlow<NewsEvent> = _conversion
    var breakingNewsPage = 1

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = NewsEvent.Loading
            when(val response = remoteRepository.getBreakingNews(countryCode, breakingNewsPage)){
                is Resource.Error -> _conversion.value = NewsEvent.Failure(response.message!!)
                is Resource.Success -> {
//                    breakingNewsPage++
//                    if(breakingNewsResponse == null){
//                        breakingNewsResponse = response.data
////                        Log.d("debug", "breakingNewsResponse is not null now")
////                        Log.d("debug", breakingNewsResponse.toString())
//                    } else{
//                        val oldArticles = breakingNewsResponse?.articles
//                        val newArticles = response.data?.articles
//                        oldArticles?.addAll(newArticles!!)
//                        breakingNewsResponse!!.articles.addAll(oldArticles!!)
//                        val temp = oldArticles!!
//                        Log.d("debug", temp.size.toString())
//                    }
//                    _conversion.value = response.data?.let { NewsEvent.Success("Success", breakingNewsResponse!!) }!!
                    response.data?.let { resultResponse ->
                        breakingNewsPage++
                        if(breakingNewsResponse == null) {
                            breakingNewsResponse = resultResponse
                        } else {
                            val oldArticles = breakingNewsResponse?.articles
                            val newArticles = resultResponse.articles
                            oldArticles?.addAll(newArticles)
                            Log.d("debug", oldArticles!!.size.toString())
                        }
                        _conversion.value = response.data.let { NewsEvent.Success("Success", breakingNewsResponse ?: resultResponse) }!!
                    }
                }
            }
        }
    }

}