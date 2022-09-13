package com.example.newsapp.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.local.entity.Article
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.presentation.view.adapter.SavedNewsAdapter
import com.example.newsapp.presentation.viewmodel.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    private val TAG = SavedNewsFragment::class.qualifiedName
    private lateinit var bindingSavedNews: FragmentSavedNewsBinding
    private val viewModel by viewModels<SavedNewsViewModel>()
    lateinit var savedNewsAdapter: SavedNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingSavedNews = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return bindingSavedNews.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRecyclerView()
    }

    private fun startRecyclerView(){
        setupRecyclerView()
        recyclerviewItemClickListener()
        onSwipeDelete()
        fillRecyclerView()
    }

    private fun recyclerviewItemClickListener(){
        savedNewsAdapter.setOnItemClickListener {
            val remoteArticle = localToRemote(it)
            val bundle = Bundle().apply {
                putSerializable("article", remoteArticle)
                if(remoteArticle.source?.id == null)
                    remoteArticle.source?.id = remoteArticle.source?.name.toString()
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun fillRecyclerView(){
        viewModel.getSavedNews()
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect{event->
                when(event){
                    is SavedNewsViewModel.NewsEvent.Success ->{
                        bindingSavedNews.progressBar.isVisible = false
                        savedNewsAdapter.differ.submitList(event.listOfArticles)
                    }
                    is SavedNewsViewModel.NewsEvent.Failure ->{
                        event.errorText.let {
                            bindingSavedNews.progressBar.isVisible = false
                            Log.e(TAG, "fillRecyclerView: the request returned with an error: $it", )
                        }
                    }
                    is SavedNewsViewModel.NewsEvent.Loading ->{
                        bindingSavedNews.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun onSwipeDelete(){
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = savedNewsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(), "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                        startRecyclerView()
                    }
                }.show()
                startRecyclerView()
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(bindingSavedNews.rvSavedNews)
        }

    }

    private fun localToRemote(localArticle: Article): com.example.newsapp.data.remote.model.Article {
        val localSource = localArticle.source
        val remoteSource = com.example.newsapp.data.remote.model.Source(
            id = localSource!!.id,
            name = localSource.name
        )

        return com.example.newsapp.data.remote.model.Article(
            author = localArticle.author,
            content = localArticle.content,
            description = localArticle.description,
            publishedAt = localArticle.publishedAt,
            source = remoteSource,
            title = localArticle.title,
            url = localArticle.url,
            urlToImage = localArticle.urlToImage
        )
    }

    private fun setupRecyclerView() {
        savedNewsAdapter = SavedNewsAdapter()
        bindingSavedNews.rvSavedNews.apply {
            adapter = savedNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}