package com.arctouch.codechallenge.ui.details

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_details_view.*
import org.apache.commons.lang3.math.NumberUtils

class DetailsView : AppCompatActivity(), DetailsViewInterface {

    private lateinit var detailsPresenter: DetailsPresenter
    private val movieImageUrlBuilder = MovieImageUrlBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_view)

        detailsPresenter = DetailsPresenter(this)
        detailsPresenter.getMovie(intent.getIntExtra("MOVIE_ID", NumberUtils.INTEGER_ZERO))
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun displayMovie(movie: Movie) {
        Glide.with(imageViewBackdrop)
                .load(movie.backdropPath?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(imageViewBackdrop)
        Glide.with(imageViewPoster)
                .load(movie.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(imageViewPoster)

        textViewTitle.text = movie.title
        textViewGenres.text = movie.genres?.joinToString(separator = ", ") { it.name }

        // Date
        val splitedReleaseDate = movie.releaseDate!!.split("-")
        val releaseDate = splitedReleaseDate[NumberUtils.INTEGER_TWO] + "/" + splitedReleaseDate[NumberUtils.INTEGER_ONE] + "/" + splitedReleaseDate[NumberUtils.INTEGER_ZERO]
        textViewReleaseDate.text = "Data de Lançamento: ${releaseDate}"
        textViewOverview.text = movie.overview
    }

    override fun displayError(errorText: String) {
        Toast.makeText(this@DetailsView, errorText, Toast.LENGTH_SHORT).show()
    }
}
