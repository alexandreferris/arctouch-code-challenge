package com.arctouch.codechallenge.ui.details

import com.arctouch.codechallenge.api.ApiClient
import com.arctouch.codechallenge.api.ApiInterface
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

open class DetailsPresenter(val detailsViewInterface: DetailsViewInterface): DetailsPresenterInterface {

    private val TAG = "DetailsPresenter"

    override fun getMovie(movieId: Int) {
        detailsViewInterface.showProgressBar()
        getObservable(movieId).subscribeWith(getObserver())
    }

    fun getObservable(movieId: Int): Observable<Movie> {
        return ApiClient.getRetrofit().movie(movieId.toLong(), ApiInterface.API_KEY, ApiInterface.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getObserver(): DisposableObserver<Movie> {
        return object : DisposableObserver<Movie>() {

            override fun onNext(movie: Movie) {
                detailsViewInterface.displayMovie(movie)
            }

            override fun onError(throwable: Throwable) {
                detailsViewInterface.displayError("Error fetching details from the selected Movie.")
            }

            override fun onComplete() {
                detailsViewInterface.hideProgressBar()
            }
        }
    }
}