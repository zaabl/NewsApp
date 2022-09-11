package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    sealed class NewEvent{
        class Success(val resultText: String): NewEvent()
        class Failure(val errorText: String): NewEvent()
        object Loading: NewEvent()
        object Empty: NewEvent()
    }

    private val _conversion = MutableStateFlow<NewEvent>(NewEvent.Empty)
    val conversion: StateFlow<NewEvent> = _conversion

    fun loadBreakingNews(countryCode: String, pageNumber: Int){
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = NewEvent.Loading
            when(val ratesResponse = remoteRepository.getBreakingNews(countryCode, pageNumber)){
                is Resource.Error -> _conversion.value = NewEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    _conversion.value = NewEvent.Success("Success")
                }
            }
        }
    }

}