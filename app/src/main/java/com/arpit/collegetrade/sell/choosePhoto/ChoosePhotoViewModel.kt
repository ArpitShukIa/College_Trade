package com.arpit.collegetrade.sell.choosePhoto

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.R
import com.arpit.collegetrade.sell.choosePhoto.SomeEvent.*
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

enum class SomeEvent { CAMERA_INTENT, GALLERY_INTENT, NAVIGATE, ERROR_MSG }

class ChoosePhotoViewModel(private val application: Application) : ViewModel() {

    var currentPhotoPath: String? = null

    private val _imageUri = MutableLiveData<Uri?>(null)
    val imageUri: LiveData<Uri?> = _imageUri

    private val _event = MutableLiveData<Event<SomeEvent>>()
    val event: LiveData<Event<SomeEvent>> = _event

    private val defaultColor =
        ContextCompat.getColor(application.applicationContext, R.color.adImageBack)

    private val _backgroundColor = MutableLiveData(defaultColor)
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun setImageUri(uri: String) {
        _imageUri.value = uri.toUri()
    }

    fun compressImage(file: File) {
        coroutineScope.launch {
            launch {
                _backgroundColor.postValue(
                    try {
                        Palette.from(BitmapFactory.decodeFile(file.path)).generate()
                            .getLightMutedColor(defaultColor)
                    } catch (e: Error) {
                        defaultColor
                    } catch (e: Exception) {
                        defaultColor
                    }
                )
            }
            launch {
                try {
                    val compressedImageFile =
                        Compressor.compress(application.applicationContext, file)
                    val uri = Uri.fromFile(compressedImageFile)
                    _imageUri.postValue(uri)
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}