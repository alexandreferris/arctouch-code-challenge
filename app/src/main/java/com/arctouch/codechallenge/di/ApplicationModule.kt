package com.arctouch.codechallenge.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ApplicationModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }


}