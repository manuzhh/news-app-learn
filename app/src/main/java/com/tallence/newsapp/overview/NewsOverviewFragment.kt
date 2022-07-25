package com.tallence.newsapp.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tallence.newsapp.databinding.FragmentOverviewBinding
import com.tallence.newsapp.searchbar.listener.SearchbarListener
import com.tallence.newsapp.utils.Constants.NewsApi.PAGE_SIZE
import com.tallence.newsapp.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsOverviewFragment : Fragment() {

    private val viewModel: NewsOverviewViewModel by viewModels()
    private lateinit var binding: FragmentOverviewBinding

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val newsScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                isLoading = true
                viewModel.paginate()
                isScrolling = false
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentOverviewBinding.inflate(inflater)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.clickListener = SearchbarListener {
            hideKeyboard()
            binding.search.searchField.clearFocus()
            viewModel.viewModelScope.launch {
                viewModel.newsRepo.resetNewsList()
                viewModel.newsRepo.setDefaultPage()
                viewModel.newsRepo.setSearchTerm(it)
                viewModel.newsRepo.loadNewsOverview(it)
            }
        }

        initViews()
        setupBinding()

        return binding.root
    }

    private fun setupBinding() {
        viewModel.newsRepo.news.observe(viewLifecycleOwner) { articleList ->
            articleList?.let {
                viewModel.viewModelScope.launch {
                    val totalPages = viewModel.newsRepo.getCurrentTotalResults() / PAGE_SIZE + 2
                    isLastPage = viewModel.newsRepo.getCurrentPage() == totalPages
                    (binding.newsList.adapter as NewsAdapter).differ.submitList(articleList)
                    isLoading = false
                }
            }
        }

        viewModel.navigateToSelectedArticle.observe(viewLifecycleOwner) { article ->
            if (null != article) {
                this.findNavController()
                    .navigate(NewsOverviewFragmentDirections.actionShowDetail(article))
                viewModel.displayArticleDetailsComplete()
            }
        }
    }

    private fun initViews() {
        val adapter = NewsAdapter(NewsAdapter.NewsListener {
            viewModel.displayArticleDetails(it)
        })
        binding.newsList.adapter = adapter
        binding.newsList.addOnScrollListener(newsScrollListener)
    }
}