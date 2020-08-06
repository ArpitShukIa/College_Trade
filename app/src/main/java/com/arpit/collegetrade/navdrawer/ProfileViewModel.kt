package com.arpit.collegetrade.navdrawer

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arpit.collegetrade.Application
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class ProfileViewModel(private val application: Application) : ViewModel() {

    private val TAG = "TAG ProfileViewModel"

    private val user = MutableLiveData(application.currentUser)

    val imageUri = user.map { it.photo }
    val name = user.map { it.name }
    val email = user.map { it.email }
    val number = user.map { it.contactNumber }

    val isNumberAvailable = number.map { it?.isNotEmpty() }

    fun compressFile(file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val compressedImageFile =
                        Compressor.compress(application.applicationContext, file)
                    val uri = Uri.fromFile(compressedImageFile)

                    application.currentUser.photo = uri.toString()
                    user.postValue(application.currentUser)
                    application.adRepository.updateUserInfo(user.value!!)
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e)
                }
            }
        }
    }

    fun updatePhoneNumber(number: String) {
        val phone = "+91 " + number.drop(3)
        application.currentUser.apply {
            contactNumber = phone
            if(!numbers.contains(phone))
                numbers.add(phone)
            user.value = this
            viewModelScope.launch {
                application.adRepository.updateUserInfo(this@apply)
            }
        }
    }
}