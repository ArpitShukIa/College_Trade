package com.example.collegetrade.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.collegetrade.databinding.FragmentHomeBinding
import com.example.collegetrade.favorites.HomeFavSharedViewModel
import com.example.collegetrade.util.adjustResize
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
        adjustResize(requireActivity(), false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.adsRecyclerView.adapter = AdsAdapter(viewModel)

        binding.scrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            val view = binding.scrollViewChild
            val diff = view.bottom - (v.height + scrollY)
            if (diff <= 0)
                viewModel.getAds()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}