package com.arpit.collegetrade.myads

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.arpit.collegetrade.databinding.FragmentMyAdsBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.home.AdsAdapter
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.setUpNavigationDrawer

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

        savedInstanceState?.let {
            if (it["isDrawerOpen"] == true)
                binding.drawer.openDrawer(GravityCompat.START)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(binding.drawer.isDrawerOpen(GravityCompat.START))
                        binding.drawer.closeDrawer(GravityCompat.START)
                    else
                        findNavController().navigateUp()
                }
            })

        binding.drawerIcon.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        binding.myAdsList.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    GridLayoutManager(context, 2)
                else
                    GridLayoutManager(context, 4)

            adapter = AdsAdapter(viewModel)
        }

        setUpNavigationDrawer(binding.navigationDrawer, this)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.drawer.let {
            val isDrawerOpen = it.isDrawerOpen(GravityCompat.START)
            outState.putBoolean("isDrawerOpen", isDrawerOpen)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}