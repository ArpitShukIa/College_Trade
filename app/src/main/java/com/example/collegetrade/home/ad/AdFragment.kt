package com.example.collegetrade.home.ad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.R
import com.example.collegetrade.data.Ad
import com.example.collegetrade.databinding.FragmentAdBinding
import com.example.collegetrade.util.getViewModelFactory
import com.example.collegetrade.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdFragment : Fragment() {

    private val TAG = "TAG AdFragment"

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

        binding.closeIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.shareIcon.setOnClickListener {
            shareAd()
        }

        try {
            val ad = args.ad
            setUpLayout(ad)
        } catch (e: Exception) {
            binding.progressLayout.visibility = View.VISIBLE
            val adId = arguments?.getString("adId")!!
            lifecycleScope.launch {
                val ad = viewModel.getAd(adId)
                if (ad == null) {
                    requireActivity().onBackPressed()
                    showToast(requireContext(), "Invalid Link")
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressLayout.visibility = View.GONE
                        setUpLayout(ad)
                    }
                }
            }
        }

        return binding.root
    }

    private fun setUpLayout(ad: Ad) {
        binding.ad = ad
        if (ad.sellerId == Firebase.auth.uid) {
            binding.moreIcon.visibility = View.GONE
            binding.viewLikesLayout.visibility = View.VISIBLE
            binding.btnChat.visibility = View.GONE
        } else {
            binding.editIcon.visibility = View.GONE
        }

        binding.editIcon.setOnClickListener {
            val directions = AdFragmentDirections.actionAdFragmentToPostAdFlow(ad)
            findNavController().navigate(directions)
        }
    }

    private fun shareAd() {
        val text = getString(R.string.share_text, binding.ad?.title, binding.ad?.id)
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