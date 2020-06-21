package com.example.collegetrade.home.ad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.EventObserver
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentAdBinding
import com.example.collegetrade.util.getViewModelFactory
import com.example.collegetrade.util.showToast
import com.like.LikeButton
import com.like.OnLikeListener


class AdFragment : Fragment() {

    private val args: AdFragmentArgs by navArgs()

    private val viewModel: AdViewModel by viewModels { getViewModelFactory() }

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

        try {
            viewModel.updateAd(args.ad)
        } catch (e: Exception) {
            binding.progressLayout.visibility = View.VISIBLE
            val adId = arguments?.getString("adId")!!
            viewModel.getAd(adId)
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
                viewModel.updateFavList(true)
                val ad = viewModel.ad.value!!
                ad.likesCount++
                ad.isLiked = true
                viewModel.ad.value = ad
            }

            override fun unLiked(likeButton: LikeButton?) {
                viewModel.updateFavList(false)
                val ad = viewModel.ad.value!!
                ad.likesCount--
                ad.isLiked = false
                viewModel.ad.value = ad
            }
        })
    }

    private fun shareAd() {
        val text = getString(R.string.share_text, viewModel.ad.value!!.title, viewModel.ad.value!!.id)
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