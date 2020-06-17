package com.example.collegetrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.collegetrade.home.HomeViewModel
import com.example.collegetrade.sell.choosePhoto.ChoosePhotoViewModel
import com.example.collegetrade.sell.reviewDetails.ReviewDetailsViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = with(modelClass) {
        when {
            isAssignableFrom(ReviewDetailsViewModel::class.java) ->
                ReviewDetailsViewModel(application)

            isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(application)

            isAssignableFrom(ChoosePhotoViewModel::class.java) ->
                ChoosePhotoViewModel(application)

            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}