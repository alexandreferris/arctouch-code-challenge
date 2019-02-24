package com.arctouch.codechallenge.ui.home

interface HomePresenterInterface {
    fun getMovies(page: Long,  searchQuery: String)
}