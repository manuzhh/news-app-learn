package com.tallence.newsapp.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tallence.newsapp.databinding.FragmentDetailBinding
import com.tallence.newsapp.overview.model.Article
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var article: Article
    private var webViewOn: Boolean = false
    private val viewModel: DetailViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        article = DetailFragmentArgs.fromBundle(requireArguments()).selectedArticle

        val binding = FragmentDetailBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.article = article

        viewModel.article.value = article

        webViewOn = savedInstanceState?.getBoolean("webViewOn", webViewOn) == true
        initWebView(binding)

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(binding: FragmentDetailBinding) {
        webView = binding.detailWebview
        webView.settings.javaScriptEnabled = true
        webView.visibility = View.GONE

        webView.webViewClient = WebViewClient()

        binding.detailContent.setOnClickListener {
            webViewOn = true
            displayWebView(binding)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webViewOn = false
                isEnabled = false
                activity?.onBackPressed()
            }
        })
        if (webViewOn)
            displayWebView(binding)
    }

    private fun displayWebView (binding: FragmentDetailBinding){
        val url = DetailFragmentArgs.fromBundle(requireArguments()).selectedArticle.url
        webView.loadUrl(url)
        binding.detailContent.visibility = View.GONE
        binding.detailDate.visibility = View.GONE
        binding.detailDescription.visibility = View.GONE
        binding.detailImage.visibility = View.GONE
        binding.detailTitle.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("webViewOn", webViewOn)
    }

}