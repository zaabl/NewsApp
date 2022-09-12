package com.example.newsapp.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.data.remote.model.Article
import com.example.newsapp.data.remote.model.Source
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.presentation.view.adapter.NewsAdapter
import com.example.newsapp.presentation.viewmodel.BreakingNewsViewModel
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
                        newsAdapter.differ.submitList(event.listOfArticles)
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
        }
    }
}