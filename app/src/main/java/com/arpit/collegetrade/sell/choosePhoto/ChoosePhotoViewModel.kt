package com.arpit.collegetrade.sell.choosePhoto

import android.app.Application
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.R
import com.arpit.collegetrade.sell.choosePhoto.SomeEvent.*
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class SomeEvent { CAMERA_INTENT, GALLERY_INTENT, NAVIGATE, ERROR_MSG }

class ChoosePhotoViewModel(application: Application) : ViewModel() {

    var currentPhotoPath: String? = null

    val context = application.applicationContext!!

    private val _imageUri = MutableLiveData<Uri?>(null)
    val imageUri: LiveData<Uri?> = _imageUri

    private val _event = MutableLiveData<Event<SomeEvent>>()
    val event: LiveData<Event<SomeEvent>> = _event

    var backgroundColor = ContextCompat.getColor(context, R.color.adImageBack)

    fun setImageUri(uri: String) {
        _imageUri.value = uri.toUri()
    }

    fun compressImage(file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val compressedImageFile = Compressor.compress(context, file)
                    val uri = Uri.fromFile(compressedImageFile)
                    _imageUri.postValue(uri)
                    backgroundColor = ContextCompat.getColor(context, R.color.primary15)
                } catch (e: Exception) {
                    _event.postValue(Event(ERROR_MSG))
                }
            }
        }
    }

    fun captureImage() {
        _event.value = Event(CAMERA_INTENT)
    }

    fun pickFromGallery() {
        _event.value = Event(GALLERY_INTENT)
    }

    fun navigate() {
        _event.value = Event(NAVIGATE)
    }
}