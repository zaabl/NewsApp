package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val localRepository: LocalRepository
) :ViewModel() {

    fun saveArticle(article: Article) = viewModelScope.launch{
        localRepository.insertArticle(article)
    }
}