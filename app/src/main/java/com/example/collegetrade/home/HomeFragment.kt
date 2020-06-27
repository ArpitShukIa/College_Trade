package com.example.collegetrade.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.collegetrade.data.Ad
import com.example.collegetrade.databinding.FragmentHomeBinding
import com.example.collegetrade.favorites.HomeFavSharedViewModel
import com.example.collegetrade.util.getViewModelFactory

class HomeFragment : Fragment() {

    private val viewModel: HomeFavSharedViewModel by activityViewModels { getViewModelFactory() }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        handleDeepLink()

        binding.adsRecyclerView.adapter = AdsAdapter(viewModel)

        binding.scrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            val view = binding.scrollViewChild
            val diff = view.bottom - (v.height + scrollY)
            if (diff <= 0)
                viewModel.getAds()
        }
        return binding.root
    }

    private fun handleDeepLink() {
        val adId = requireActivity().intent.getStringExtra("adId")
        if(!adId.isNullOrEmpty() && !viewModel.isDeepLinkHandled) {
            val ad = Ad(id = adId)
            val directions = HomeFragmentDirections.actionHomeFragmentToAdFragment(ad)
            findNavController().navigate(directions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}