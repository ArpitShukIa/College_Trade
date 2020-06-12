package com.example.collegetrade.sell.choosePhoto

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.example.collegetrade.EventObserver
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentChoosePhotoBinding
import com.example.collegetrade.sell.choosePhoto.SomeEvent.*
import com.example.collegetrade.util.showSnackBar
import com.google.android.material.snackbar.Snackbar
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
        ChoosePhotoViewModelFactory(requireActivity().application)
    }

    private val CAMERA_INTENT_REQUEST_CODE = 1
    private val GALLERY_INTENT_REQUEST_CODE = 2
    private val READ_PERMISSION_REQUEST_CODE = 3

    private var displayPermissionDenialMsg = true

    private val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                CAMERA_INTENT -> launchCameraIntent()
                GALLERY_INTENT -> checkPermissionAndLaunchGalleryIntent()
                NAVIGATE -> navigate()
                ERROR_MSG -> showSnackBar(
                    binding.btnNext,
                    getString(R.string.some_error_occurred_msg)
                )
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

    private fun checkPermissionAndLaunchGalleryIntent() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                readPermission
            ) == PackageManager.PERMISSION_GRANTED -> launchGalleryIntent()

            shouldShowRequestPermissionRationale(readPermission) -> {
                displayPermissionDenialMsg = true
                showInfoDialog(1)
            }

            else -> {
                displayPermissionDenialMsg = false
                requestPermissions(arrayOf(readPermission), READ_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showInfoDialog(code: Int) {
        MaterialDialog(requireContext())
            .show {
                title(R.string.permission_dialog_title)
                message(R.string.permission_dialog_message)
                positiveButton(R.string.proceed) {
                    if (code == 1) {
                        requestPermissions(arrayOf(readPermission), READ_PERMISSION_REQUEST_CODE)
                    } else {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireContext().packageName, null)
                            startActivity(this)
                        }
                    }
                }
                negativeButton(R.string.no_thanks)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGalleryIntent()
            } else if (shouldShowRequestPermissionRationale(readPermission) || displayPermissionDenialMsg) {
                showSnackBar(
                    binding.btnNext,
                    getString(R.string.permission_denial_message),
                    Snackbar.LENGTH_LONG
                )
            } else {
                showInfoDialog(2)
            }
        }
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
                        viewModel.currentPhotoPath?.let {
                            val file = File(it)
                            viewModel.compressImage(file)
                        }
                    }

                    GALLERY_INTENT_REQUEST_CODE -> {
                        val uri = data?.data!!
                        val path = getPath(uri)
                        path?.let {
                            val file = File(it)
                            viewModel.compressImage(file)
                        }
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

    @Suppress("DEPRECATION")
    private fun getPath(uri: Uri): String? {
        return try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(requireContext(), uri, projection, null, null, null)
            val cursor = loader.loadInBackground()!!
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex).also {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "getPath: ${e.stackTrace}", e)
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}