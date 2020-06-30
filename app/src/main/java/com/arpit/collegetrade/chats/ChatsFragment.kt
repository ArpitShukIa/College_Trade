package com.arpit.collegetrade.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arpit.collegetrade.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        
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
