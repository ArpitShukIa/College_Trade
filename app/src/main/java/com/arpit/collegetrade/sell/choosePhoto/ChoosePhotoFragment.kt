package com.arpit.collegetrade.sell.choosePhoto

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.arpit.collegetrade.EventObserver
import com.arpit.collegetrade.R
import com.arpit.collegetrade.databinding.FragmentChoosePhotoBinding
import com.arpit.collegetrade.sell.choosePhoto.SomeEvent.*
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.showSnackBar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.*

class ChoosePhotoFragment : Fragment() {

    private val TAG = "TAG ChoosePhotoFragment"

    private val args: ChoosePhotoFragmentArgs by navArgs()

    private var _binding: FragmentChoosePhotoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChoosePhotoViewModel by navGraphViewModels(R.id.adDetailsFlow) {
        getViewModelFactory()
    }

    private val CAMERA_INTENT_REQUEST_CODE = 1
    private val GALLERY_INTENT_REQUEST_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if (viewModel.imageUri.value == null)
            viewModel.setImageUri(args.ad.image)

        binding.adImage.setOnClickListener {
            val uri = viewModel.imageUri.value.toString()
            if (uri.isNotEmpty()) {
                val directions =
                    ChoosePhotoFragmentDirections.actionChoosePhotoFragmentToZoomPhotoFragment(uri)
                findNavController().navigate(directions)
            }
        }

        viewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                CAMERA_INTENT -> launchCameraIntent()
                GALLERY_INTENT -> launchGalleryIntent()
                NAVIGATE -> navigate()
                ERROR_MSG -> showSnackBar(binding.btnNext, getString(R.string.something_went_wrong))
            }
        })

        return binding.root
    }

    private fun launchCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            Timber.tag(TAG).e(e)
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.arpit.collegetrade",
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
        val ad = args.ad
        ad.image = imageUri.toString()
        val directions =
            ChoosePhotoFragmentDirections.actionChoosePhotoFragmentToSetPriceFragment(ad)
        findNavController().navigate(directions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_INTENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK)
                    viewModel.currentPhotoPath?.let {
                        val file = File(it)
                        launchImageCrop(file.toUri())
                    }
                else
                    Timber.tag(TAG).e("Image Failed to Load")
            }

            GALLERY_INTENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK)
                    data?.data?.let {
                        launchImageCrop(it)
                    }
                else
                    Timber.tag(TAG).e("Image Failed to load")
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val uri = result.uri
                    try {
                        val file = File(uri.path!!)
                        viewModel.compressImage(file)
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e)
                    }
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Timber.tag(TAG).e(result.error)
                }
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

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}