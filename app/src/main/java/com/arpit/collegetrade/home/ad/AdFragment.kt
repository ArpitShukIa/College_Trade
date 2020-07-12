package com.arpit.collegetrade.home.ad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arpit.collegetrade.EventObserver
import com.arpit.collegetrade.R
import com.arpit.collegetrade.databinding.FragmentAdBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.showToast
import com.like.LikeButton
import com.like.OnLikeListener

// TODO Provide Chat, Mark as Sold & Report Ad options

class AdFragment : Fragment() {

    private val args: AdFragmentArgs by navArgs()

    private val viewModel: AdViewModel by viewModels { getViewModelFactory() }
    private val sharedViewModel: SharedViewModel by activityViewModels { getViewModelFactory() }

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setUpListeners()

        val ad = args.ad
        if (ad.sellerId.isEmpty() && !sharedViewModel.isDeepLinkHandled) {
            binding.progressLayout.visibility = View.VISIBLE
            viewModel.getAd(ad.id)
            sharedViewModel.isDeepLinkHandled = true
        } else if (ad.sellerId.isNotEmpty()) {
            viewModel.updateAd(ad)
        }

        viewModel.goBackEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireActivity().onBackPressed()
                showToast(requireContext(), "Invalid Link")
            } else {
                binding.progressLayout.visibility = View.GONE
            }
        })

        return binding.root
    }

    private fun setUpListeners() {
        binding.closeIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.shareIcon.setOnClickListener {
            shareAd()
        }

        binding.editIcon.setOnClickListener {
            val directions = AdFragmentDirections.actionAdFragmentToPostAdFlow(viewModel.ad.value!!)
            findNavController().navigate(directions)
        }

        binding.favoriteIcon.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                val ad = viewModel.ad.value!!
                ad.likesCount++
                ad.likeTime = sharedViewModel.currentTime.value ?: 0
                viewModel.ad.value = ad
                sharedViewModel.updateFavList(ad, true)
            }

            override fun unLiked(likeButton: LikeButton?) {
                val ad = viewModel.ad.value!!
                sharedViewModel.updateFavList(ad, false)
                ad.likesCount--
                ad.likeTime = 0L
                viewModel.ad.value = ad
            }
        })

        binding.adImage.setOnClickListener {
            val uri = viewModel.ad.value?.image ?: ""
            if (uri.isNotEmpty()) {
                val directions = AdFragmentDirections.actionAdFragmentToZoomPhotoFragment(uri)
                findNavController().navigate(directions)
            }
        }

        binding.btnChat.setOnClickListener {
            val directions = AdFragmentDirections.actionAdFragmentToChatFragment()
            findNavController().navigate(directions)
        }
    }

    private fun shareAd() {
        val text =
            getString(R.string.share_text, viewModel.ad.value!!.title, viewModel.ad.value!!.id)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}