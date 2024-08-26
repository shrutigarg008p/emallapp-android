package com.emall.net.network.api

import com.emall.net.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiInterface {

    private val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.ECOMMERCE_STAGING_URL)
                .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}