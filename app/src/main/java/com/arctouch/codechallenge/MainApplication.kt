package com.arctouch.codechallenge

import android.app.Application
import com.arctouch.codechallenge.api.ApiClient
import com.arctouch.codechallenge.api.ApiInterface
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.di.ApplicationComponent
import com.arctouch.codechallenge.di.ApplicationModule
import com.arctouch.codechallenge.di.DaggerApplicationComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class MainApplication: Application() {
    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

        ApiClient.getRetrofit().genres(ApiInterface.API_KEY, ApiInterface.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Cache.cacheGenres(it.genres)
                }
    }

    fun getApplicationComponent(): ApplicationComponent {
        return applicationComponent
    }
}