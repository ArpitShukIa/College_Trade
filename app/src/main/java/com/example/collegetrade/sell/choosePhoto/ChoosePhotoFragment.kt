package com.example.collegetrade.sell.choosePhoto

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentChoosePhotoBinding
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.*

class ChoosePhotoFragment : Fragment(), View.OnClickListener {

    private val TAG = "ChoosePhotoFragment"

    private val args: ChoosePhotoFragmentArgs by navArgs()

    private lateinit var binding: FragmentChoosePhotoBinding

    private lateinit var currentPhotoPath: String
    private lateinit var imageUri: Uri

    private val CAMERA_INTENT_REQUEST_CODE = 1
    private val GALLERY_INTENT_REQUEST_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)

        try {
            imageUri = savedInstanceState?.getString("adImage")!!.toUri()
            Glide.with(this)
                .load(imageUri)
                .into(binding.adImage)
            activateButtonNext()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnCamera.setOnClickListener(this)
        binding.btnGallery.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_camera -> launchCameraIntent()

            R.id.btn_gallery -> {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                    startActivityForResult(it, GALLERY_INTENT_REQUEST_CODE)
                }
            }

            R.id.btn_next -> {
                Toast.makeText(requireActivity(), "Image Captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.collegetrade",
                it
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_INTENT_REQUEST_CODE -> {
                    try {
                        File(currentPhotoPath).also {
                            setImage(it)
                            activateButtonNext()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireActivity(),
                            "Some error occurred. Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "onActivityResult: ${e.stackTrace}", e)
                    }
                }

                GALLERY_INTENT_REQUEST_CODE -> {
                    imageUri = data?.data!!
                    getImageFile(imageUri)?.also {
                        setImage(it)
                        activateButtonNext()
                    }
                }
            }
        }
    }

    private fun activateButtonNext() {
        binding.btnNext.isEnabled = true
        binding.btnNext.alpha = 1.0f
        binding.btnCamera.alpha = 0.8f
        binding.btnGallery.alpha = 0.8f
    }

    private fun setImage(f: File) {
        GlobalScope.launch {
            val compressedImageFile = Compressor.compress(requireContext(), f)
            imageUri = Uri.fromFile(compressedImageFile)
            withContext(Dispatchers.Main) {
                Glide.with(this@ChoosePhotoFragment)
                    .load(Uri.fromFile(f))
                    .into(binding.adImage)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = DateFormat.getDateTimeInstance().format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun getImageFile(imageUri: Uri): File? {
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            return null
        }
        val inputStream =
            requireActivity().contentResolver.openInputStream(imageUri)
        val fileOutputStream = FileOutputStream(photoFile)
        val buffer = ByteArray(1024)
        while (true) {
            val bytesRead = inputStream?.read(buffer)!!
            if (bytesRead == -1) break
            fileOutputStream.write(buffer, 0, bytesRead)
        }
        fileOutputStream.close()
        inputStream.close()
        return photoFile
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::imageUri.isInitialized)
            outState.putString("adImage", imageUri.toString())
    }
}