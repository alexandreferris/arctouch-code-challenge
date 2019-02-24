package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.model.Movie

interface HomeViewInterface {
    fun showProgressBar()
    fun hideProgressBar()
    fun displayMovies(movies: ArrayList<Movie>, visibleThreshold: Int)
    fun displayError(errorText: String)
}