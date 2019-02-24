package com.arctouch.codechallenge.ui.details

import com.arctouch.codechallenge.model.Movie

interface DetailsViewInterface {
    fun showProgressBar()
    fun hideProgressBar()
    fun displayMovie(movie: Movie)
    fun displayError(errorText: String)
}