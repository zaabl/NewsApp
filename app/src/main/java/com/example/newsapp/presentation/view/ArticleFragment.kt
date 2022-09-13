package com.example.newsapp.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.data.local.entity.Source
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.presentation.viewmodel.ArticleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {

    val args: ArticleFragmentArgs by navArgs()
    private lateinit var bindingArticle: FragmentArticleBinding
    private val viewModel by viewModels<ArticleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingArticle = FragmentArticleBinding.inflate(inflater, container, false)
        return bindingArticle.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = args.article
        bindingArticle.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }
        saveArticle()
    }

    fun saveArticle(){
        bindingArticle.fab.setOnClickListener {
            val remoteSource = args.article.source
            val localSource = Source(
                id = remoteSource!!.id,
                name = remoteSource.name
            )
            val remoteArticle = args.article
            val localArticle = Article(
                author = remoteArticle.author,
                content = remoteArticle.content,
                description = remoteArticle.description,
                publishedAt = remoteArticle.publishedAt,
                source = localSource,
                title = remoteArticle.title,
                url = remoteArticle.url,
                urlToImage = remoteArticle.urlToImage
            )

            viewModel.saveArticle(localArticle)
            Snackbar.make(requireView(), "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}