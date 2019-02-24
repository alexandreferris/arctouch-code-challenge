package com.arctouch.codechallenge.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

open class ApiClient {

    companion object {
        fun getRetrofit(): ApiInterface {
            val retrofitApi: ApiInterface = Retrofit.Builder()
                    .baseUrl(ApiInterface.URL)
                    .client(OkHttpClient.Builder().build())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(ApiInterface::class.java)

            return retrofitApi
        }
    }
}