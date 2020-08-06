package com.arpit.collegetrade.navdrawer

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.arpit.collegetrade.MainActivity
import com.arpit.collegetrade.databinding.FragmentProfileBinding
import com.arpit.collegetrade.util.getViewModelFactory
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private val TAG = "TAG ProfileFragment"

    private val viewModel: ProfileViewModel by activityViewModels { getViewModelFactory() }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val GALLERY_INTENT_REQUEST_CODE = 1
    private val RESOLVE_HINT = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.userImage.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                startActivityForResult(it, GALLERY_INTENT_REQUEST_CODE)
            }
        }

        binding.btnAddNumber.setOnClickListener { requestPhoneNumber() }
        binding.displayNumberLayout.setOnClickListener { requestPhoneNumber() }

        viewModel.imageUri.observe(viewLifecycleOwner, Observer {
            setUpNavigationDrawer(
                requireActivity().navigation_drawer,
                requireActivity() as MainActivity
            )
        })

        return binding.root
    }

    private fun requestPhoneNumber() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val apiClient =
            GoogleApiClient.Builder(requireContext()).addApi(Auth.CREDENTIALS_API).build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest)
        startIntentSenderForResult(intent.intentSender, RESOLVE_HINT, null, 0, 0, 0, null)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_INTENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let {
                        launchImageCrop(it)
                    }
                } else {
                    Timber.tag(TAG).d("onActivityResult: Image Failed to Load")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val uri = result.uri
                    try {
                        val file = File(uri.path!!)
                        viewModel.compressFile(file)
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e)
                    }
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Timber.tag(TAG).e(result.error)
                }
            }

            RESOLVE_HINT -> {
                if (resultCode == RESULT_OK) {
                    val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                    viewModel.updatePhoneNumber(credential?.id!!)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}