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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.EventObserver
import com.example.collegetrade.databinding.FragmentChoosePhotoBinding
import com.example.collegetrade.sell.choosePhoto.ButtonEvent.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.*

class ChoosePhotoFragment : Fragment() {

    private val TAG = "ChoosePhotoFragment"

    private val args: ChoosePhotoFragmentArgs by navArgs()

    private lateinit var binding: FragmentChoosePhotoBinding

    private val viewModel: ChoosePhotoViewModel by viewModels {
        ChoosePhotoViewModelFactory(
            requireActivity().application
        )
    }

    private val CAMERA_INTENT_REQUEST_CODE = 1
    private val GALLERY_INTENT_REQUEST_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.event.observe(viewLifecycleOwner, EventObserver { buttonEvent ->
            when (buttonEvent) {
                CAMERA_INTENT -> launchCameraIntent()
                GALLERY_INTENT -> launchGalleryIntent()
                NAVIGATE -> navigate()
            }
        })

        return binding.root
    }

    private fun launchCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            Log.e(TAG, "launchCameraIntent: ${e.stackTrace}", e)
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

    private fun launchGalleryIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, GALLERY_INTENT_REQUEST_CODE)
        }
    }

    private fun navigate() {
        val imageUri = viewModel.imageUri.value
        val adDetails = args.adDetails.plus(imageUri.toString())
        val directions =
            ChoosePhotoFragmentDirections.actionChoosePhotoFragmentToSetPriceFragment(adDetails)
        findNavController().navigate(directions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                when (requestCode) {
                    CAMERA_INTENT_REQUEST_CODE -> {
                        val file = File(viewModel.currentPhotoPath)
                        viewModel.compressImage(file)
                    }

                    GALLERY_INTENT_REQUEST_CODE -> {
                        val uri = data?.data!!
                        val file = getImageFile(uri)
                        viewModel.compressImage(file!!)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "onActivityResult: ${e.stackTrace}", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = DateFormat.getDateTimeInstance().format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir!!
        ).apply {
            viewModel.currentPhotoPath = absolutePath
        }
    }

    private fun getImageFile(imageUri: Uri): File? {
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            Log.e(TAG, "getImageFile: ${e.stackTrace}", e)
            return null
        }
        val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
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

}