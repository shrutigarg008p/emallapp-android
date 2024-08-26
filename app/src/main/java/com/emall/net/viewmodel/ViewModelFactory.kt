package com.emall.net.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emall.net.network.api.ApiService
import java.lang.IllegalArgumentException

class ViewModelFactory(private var apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}