package com.arpit.collegetrade.account.myads

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.arpit.collegetrade.databinding.FragmentMyAdsBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.home.AdsAdapter
import com.arpit.collegetrade.util.getViewModelFactory

class MyAdsFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels { getViewModelFactory() }

    private var _binding: FragmentMyAdsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAdsBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.myAdsList.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    GridLayoutManager(context, 2)
                else
                    GridLayoutManager(context, 4)

            adapter = AdsAdapter(viewModel)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}