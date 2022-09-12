package com.example.newsapp.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentBreakingNewsBinding

class ArticleFragment : Fragment(R.layout.fragment_article) {

    val args: ArticleFragmentArgs by navArgs()
    private lateinit var bindingArticle: FragmentArticleBinding

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
    }
}