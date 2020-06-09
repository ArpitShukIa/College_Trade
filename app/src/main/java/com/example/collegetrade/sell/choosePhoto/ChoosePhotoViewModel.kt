package com.example.collegetrade.sell.choosePhoto

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegetrade.Event
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

enum class ButtonEvent { CAMERA_INTENT, GALLERY_INTENT, NAVIGATE }

class ChoosePhotoViewModel(private val application: Application) : ViewModel() {

    var currentPhotoPath: String = ""

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _event = MutableLiveData<Event<ButtonEvent>>()
    val event: LiveData<Event<ButtonEvent>> = _event

    init {
        _imageUri.value = null
    }

    fun compressImage(file: File) {
        viewModelScope.launch {
            val compressedImageFile = Compressor.compress(application.applicationContext, file)
            _imageUri.value = Uri.fromFile(compressedImageFile)
        }
    }

    fun captureImage() {
        _event.value = Event(ButtonEvent.CAMERA_INTENT)
    }

    fun pickFromGallery() {
        _event.value = Event(ButtonEvent.GALLERY_INTENT)
    }

    fun navigate() {
        _event.value = Event(ButtonEvent.NAVIGATE)
    }
}