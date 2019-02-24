package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.api.ApiClient
import com.arctouch.codechallenge.api.ApiInterface
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import com.arctouch.codechallenge.model.Movie
import org.apache.commons.lang3.math.NumberUtils


open class HomePresenter(val homeViewInterface: HomeViewInterface): HomePresenterInterface {

    private var movies: ArrayList<Movie> = ArrayList()
    private var totalPages: Long = NumberUtils.LONG_MINUS_ONE
    private var currentPage: Long = NumberUtils.LONG_ONE
    var isSearchingMovies: Boolean = false

    override fun getMovies(page: Long, searchQuery: String) {
        if (totalPages == NumberUtils.LONG_MINUS_ONE || (page <= totalPages && currentPage < totalPages)) getObservable(page, searchQuery).subscribeWith(getObserver())
        currentPage = page
    }

    fun getObservable(page: Long, searchQuery: String): Observable<UpcomingMoviesResponse> {
        homeViewInterface.showProgressBar()

        if (isSearchingMovies)
            return ApiClient.getRetrofit().searchMovies(ApiInterface.API_KEY, ApiInterface.DEFAULT_LANGUAGE, searchQuery, page, ApiInterface.DEFAULT_REGION)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        else
            return ApiClient.getRetrofit().upcomingMovies(ApiInterface.API_KEY, ApiInterface.DEFAULT_LANGUAGE, page, ApiInterface.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getObserver(): DisposableObserver<UpcomingMoviesResponse> {
        return object : DisposableObserver<UpcomingMoviesResponse>() {

            override fun onNext(upcomingMoviesResponse: UpcomingMoviesResponse) {
                totalPages = upcomingMoviesResponse.totalPages.toLong()
                movies.addAll(upcomingMoviesResponse.results)
                homeViewInterface.displayMovies(movies, (upcomingMoviesResponse.totalResults / upcomingMoviesResponse.totalPages))
            }

            override fun onError(throwable: Throwable) {
                homeViewInterface.displayError("Erro fetching Movies List")
            }

            override fun onComplete() {
                homeViewInterface.hideProgressBar()
            }
        }
    }

    fun resetMoviesList() {
        movies = ArrayList()
        totalPages = NumberUtils.LONG_MINUS_ONE
        currentPage = NumberUtils.LONG_ONE
    }
}