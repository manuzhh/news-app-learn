package com.tallence.newsapp.searchbar.listener

class SearchbarListener(val clickListener: (query: String) -> Unit) {
    fun onClick(query: String) = clickListener(query)
}