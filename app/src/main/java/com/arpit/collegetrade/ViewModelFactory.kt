package com.arpit.collegetrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.home.ad.AdViewModel
import com.arpit.collegetrade.navdrawer.ProfileViewModel
import com.arpit.collegetrade.sell.choosePhoto.ChoosePhotoViewModel
import com.arpit.collegetrade.sell.reviewDetails.ReviewDetailsViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = with(modelClass) {
        when {
            isAssignableFrom(ReviewDetailsViewModel::class.java) ->
                ReviewDetailsViewModel(application)

            isAssignableFrom(ChoosePhotoViewModel::class.java) ->
                ChoosePhotoViewModel(application)

            isAssignableFrom(AdViewModel::class.java) ->
                AdViewModel(application)

            isAssignableFrom(SharedViewModel::class.java) ->
                SharedViewModel(application)

            isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(application)

            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}