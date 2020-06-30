package com.arpit.collegetrade.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.databinding.FragmentHomeBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.setUpNavigationDrawer
import kotlinx.android.synthetic.main.activity_main.*


class HomeFragment : Fragment(), DrawerLayout.DrawerListener {

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

        binding.drawer.addDrawerListener(this)

        savedInstanceState?.let {
            if (it["isDrawerOpen"] == true) {
                binding.drawer.openDrawer(GravityCompat.START)
                setGuideline(0)
            }
        }

        handleDeepLink()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.drawer.isDrawerOpen(GravityCompat.START))
                        binding.drawer.closeDrawer(GravityCompat.START)
                    else
                        requireActivity().finish()
                }
            })

        binding.drawerIcon.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        binding.adsRecyclerView.adapter = AdsAdapter(viewModel)

        binding.scrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            val view = binding.scrollViewChild
            val diff = view.bottom - (v.height + scrollY)
            if (diff <= 0)
                viewModel.getAds()
        }

        setUpNavigationDrawer(binding.navigationDrawer, this)

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

    private fun setGuideline(value: Int) {
        val params = requireActivity().guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guideEnd = value
        requireActivity().guideline.layoutParams = params
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        setGuideline((84 * (1 - slideOffset) * resources.displayMetrics.density).toInt())
    }

    override fun onDrawerStateChanged(newState: Int) {}
    override fun onDrawerClosed(drawerView: View) {}
    override fun onDrawerOpened(drawerView: View) {}

}