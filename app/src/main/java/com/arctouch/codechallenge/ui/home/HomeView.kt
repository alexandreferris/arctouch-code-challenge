package com.arctouch.codechallenge.ui.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.ui.details.DetailsView
import kotlinx.android.synthetic.main.activity_home_view.*
import org.apache.commons.lang3.math.NumberUtils
import com.arctouch.codechallenge.util.scroll.EndlessScrollListener
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.arctouch.codechallenge.model.Movie
import android.support.v7.widget.SearchView
import android.widget.Toast
import org.apache.commons.lang3.StringUtils


class HomeView : AppCompatActivity(), HomeViewInterface {

    private lateinit var homePresenter: HomePresenter
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private var startingPage: Long = NumberUtils.LONG_ONE
    private var currentPage: Long = NumberUtils.LONG_ONE
    private var searchQuery: String = StringUtils.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        homePresenter = HomePresenter(this)
        homePresenter.getMovies(currentPage, searchQuery)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchItem: MenuItem = menu!!.findItem(R.id.action_search)

        val searchView: SearchView? = searchItem.actionView as SearchView
        if (searchView != null) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    homePresenter.resetMoviesList()

                    currentPage = startingPage
                    searchQuery = query
                    homePresenter.isSearchingMovies = true
                    homePresenter.getMovies(currentPage, searchQuery)
                    return false
                }

            })
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    resetMoviesList()
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }
            })
        } else {
            // Phones running an SDK before froyo
            searchView!!.setOnCloseListener(object : SearchView.OnCloseListener {

                override fun onClose(): Boolean {
                    resetMoviesList()
                    return false
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun setupRecyclerView(movies: List<Movie>, visibleThreshold: Int) {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        homeAdapter = HomeAdapter { movieId ->
            val movieSelectedDetails = Intent(this, DetailsView::class.java)
            movieSelectedDetails.putExtra("MOVIE_ID", movieId)

            startActivity(movieSelectedDetails)
        }
        homeAdapter.setMoviesList(movies)
        recyclerView.adapter = homeAdapter

        scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                currentPage = page.toLong()
                homePresenter.getMovies(currentPage, searchQuery)
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun displayMovies(movies: ArrayList<Movie>, visibleThreshold: Int) {

        val moviesWithGenres = movies.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }

        if (currentPage == startingPage)
            setupRecyclerView(moviesWithGenres, visibleThreshold)
        else {
            homeAdapter.setMoviesList(moviesWithGenres)
            homeAdapter.notifyDataSetChanged()
        }
    }

    override fun displayError(errorText: String) {
        Toast.makeText(this@HomeView, errorText, Toast.LENGTH_SHORT).show()
    }

    private fun resetMoviesList() {
        homePresenter.resetMoviesList()

        currentPage = startingPage
        searchQuery = StringUtils.EMPTY
        homePresenter.isSearchingMovies = false
        homePresenter.getMovies(currentPage, searchQuery)
    }
}
