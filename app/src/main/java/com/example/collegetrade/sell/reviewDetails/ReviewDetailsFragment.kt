package com.example.collegetrade.sell.reviewDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.collegetrade.EventObserver
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentReviewDetailsBinding
import com.example.collegetrade.sell.reviewDetails.Actions.*
import com.example.collegetrade.util.*
import kotlinx.android.synthetic.main.progress_bar.*

class ReviewDetailsFragment : Fragment() {

    private val args: ReviewDetailsFragmentArgs by navArgs()

    private var _binding: FragmentReviewDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReviewDetailsViewModel by navGraphViewModels(R.id.adDetailsFlow) {
        getViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewDetailsBinding.inflate(inflater, container, false)
        adjustResize(requireActivity(), true)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.getAdDetails(args.adDetails)

        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .cancelable(false)
            .customView(R.layout.progress_bar)
        dialog.progress_title.text = getString(R.string.posting_ad)

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
            }
        })

        return binding.root
    }

    fun navigate() {
        val directions = ReviewDetailsFragmentDirections.actionGlobalHomeFragment()
        findNavController().navigate(directions)
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