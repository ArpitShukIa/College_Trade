package com.arpit.collegetrade.sell.reviewDetails

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.arpit.collegetrade.EventObserver
import com.arpit.collegetrade.R
import com.arpit.collegetrade.databinding.FragmentReviewDetailsBinding
import com.arpit.collegetrade.sell.reviewDetails.Actions.*
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.hideKeyboard
import com.arpit.collegetrade.util.showSnackBar
import com.arpit.collegetrade.util.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.progress_bar.*
import timber.log.Timber
import java.io.File

class ReviewDetailsFragment : Fragment() {

    private val TAG = "TAG ReviewDetailsFrag"

    private val args: ReviewDetailsFragmentArgs by navArgs()

    private var _binding: FragmentReviewDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReviewDetailsViewModel by navGraphViewModels(R.id.adDetailsFlow) {
        getViewModelFactory()
    }

    private val GALLERY_INTENT_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewDetailsBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.getAdDetails(args.ad)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .cancelable(false)
            .customView(R.layout.progress_bar)
        dialog.progress_title.text = getString(R.string.posting_ad)

        binding.sellerImage.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                startActivityForResult(it, GALLERY_INTENT_REQUEST_CODE)
            }
        }

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                EMPTY_NAME -> showSnackBar(
                    binding.btnPost,
                    getString(R.string.name_cannot_be_empty)
                )

                UPLOAD_STARTED -> dialog.show()

                UPLOAD_FAILED -> {
                    dialog.cancel()
                    showToast(requireContext(), getString(R.string.something_went_wrong))
                    showToast(requireContext(), getString(R.string.ad_not_posted))
                    navigate()
                }

                UPLOAD_SUCCEEDED -> {
                    dialog.cancel()
                    showToast(requireContext(), getString(R.string.ad_posted))
                    navigate()
                }

                IMAGE_LOADING_FAILED -> {
                    showSnackBar(
                        binding.btnPost,
                        "Image loading failed. Try again..."
                    )
                }
            }
        })

        return binding.root
    }

    fun navigate() {
        val directions = ReviewDetailsFragmentDirections.actionGlobalHomeFragment()
        findNavController().navigate(directions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_INTENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK)
                    data?.data?.let { launchImageCrop(it) }
                else
                    Timber.tag(TAG).d("onActivityResult: Image Failed to Load")
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

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(requireContext(), this)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(requireActivity(), binding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}