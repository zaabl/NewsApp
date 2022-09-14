package com.example.newsapp.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.presentation.view.adapter.NewsAdapter
import com.example.newsapp.presentation.viewmodel.BreakingNewsViewModel
import com.example.newsapp.presentation.viewmodel.SearchNewsViewModel
import com.example.newsapp.utils.Constants
import com.example.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private val TAG = BreakingNewsFragment::class.qualifiedName
    private lateinit var bindingSearchNews: FragmentSearchNewsBinding
    private val viewModel by viewModels<SearchNewsViewModel>()
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingSearchNews = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return bindingSearchNews.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        search()
    }

    private fun initializeRecyclerView(){
        setupRecyclerView()
        recyclerviewItemClickListener()
        fillRecyclerView()
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getSearchNews(bindingSearchNews.etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun recyclerviewItemClickListener(){
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
                if(it.source?.id == null)
                    it.source?.id = it.source?.name.toString()
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun search(){
        var job: Job? = null
        bindingSearchNews.etSearch.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_DELAY)
                it?.let {
                    if(it.toString().isNotEmpty()){
                        viewModel.getSearchNews(it.toString())
                        initializeRecyclerView()
                    }
                }
            }
        }
    }

    private fun fillRecyclerView(){
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect{event->
                when(event){
                    is SearchNewsViewModel.NewsEvent.Success ->{
                        bindingSearchNews.paginationProgressBar.isVisible = false
                        newsAdapter.differ.submitList(event.newsResponse.articles)
                        val totalPages = event.newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage)
                            bindingSearchNews.rvSearchNews.setPadding(0, 0, 0, 0)
                    }
                    is SearchNewsViewModel.NewsEvent.Failure ->{
                        bindingSearchNews.paginationProgressBar.isVisible = false
                        event.errorText.let {
                            Log.e(TAG, "fillRecyclerView: the request returned with an error: $it", )
                        }
                    }
                    is SearchNewsViewModel.NewsEvent.Loading ->{
                        bindingSearchNews.paginationProgressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        bindingSearchNews.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}