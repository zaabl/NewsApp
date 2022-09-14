package com.example.newsapp.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.presentation.view.adapter.NewsAdapter
import com.example.newsapp.presentation.viewmodel.BreakingNewsViewModel
import com.example.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private val TAG = BreakingNewsFragment::class.qualifiedName
    private lateinit var bindingBreakingNews: FragmentBreakingNewsBinding
    private val viewModel by viewModels<BreakingNewsViewModel>()
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingBreakingNews = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return bindingBreakingNews.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
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
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun fillRecyclerView(){
        viewModel.getBreakingNews("us")
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect{event->
                when(event){
                    is BreakingNewsViewModel.NewsEvent.Success ->{
                        bindingBreakingNews.paginationProgressBar.isVisible = false
                        newsAdapter.differ.submitList(event.newsResponse.articles.toList())
                        val totalPages = event.newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage)
                            bindingBreakingNews.rvBreakingNews.setPadding(0, 0, 0, 0)

                    }
                    is BreakingNewsViewModel.NewsEvent.Failure ->{
                        bindingBreakingNews.paginationProgressBar.isVisible = false
                        event.errorText.let {
                            Log.e(TAG, "fillRecyclerView: the request returned with an error: $it", )
                        }
                    }
                    is BreakingNewsViewModel.NewsEvent.Loading ->{
                        bindingBreakingNews.paginationProgressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        bindingBreakingNews.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}