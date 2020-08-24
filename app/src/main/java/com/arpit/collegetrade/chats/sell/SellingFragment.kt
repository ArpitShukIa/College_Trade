package com.arpit.collegetrade.chats.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.arpit.collegetrade.MainActivity
import com.arpit.collegetrade.chats.ChatsAdapter
import com.arpit.collegetrade.databinding.FragmentSellingBinding

class SellingFragment : Fragment() {

    private lateinit var viewModel: SellingViewModel

    private var _binding: FragmentSellingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellingBinding.inflate(inflater, container, false)

        viewModel = (requireActivity() as MainActivity).sellingViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.sellingChats.adapter = ChatsAdapter()

        // To remove the flash when items are updated
        val animator = binding.sellingChats.itemAnimator as SimpleItemAnimator
        animator.supportsChangeAnimations = false

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}