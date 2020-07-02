package com.arpit.collegetrade.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.databinding.FragmentHomeBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.util.getViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_bar_layout.view.*

class HomeFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels { getViewModelFactory() }

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

        binding.toolbar.drawer_icon.setOnClickListener {
            requireActivity().drawer.openDrawer(GravityCompat.START)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(requireActivity().drawer.isDrawerOpen(GravityCompat.START))
                        requireActivity().drawer.closeDrawer(GravityCompat.START)
                    else
                        requireActivity().finish()
                }

            })

        binding.adsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1))
                    viewModel.getAds()
            }
        })

        return binding.root
    }

    private fun handleDeepLink() {
        val adId = requireActivity().intent.getStringExtra("adId")
        if (!adId.isNullOrEmpty() && !viewModel.isDeepLinkHandled) {
            val ad = Ad(id = adId)
            val directions = HomeFragmentDirections.actionHomeFragmentToAdFragment(ad)
            findNavController().navigate(directions)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adsRecyclerView.layoutManager?.onRestoreInstanceState(viewModel.stateHome)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stateHome = binding.adsRecyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}