package com.arpit.collegetrade.chats.buy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.arpit.collegetrade.MainActivity
import com.arpit.collegetrade.chats.ChatsAdapter
import com.arpit.collegetrade.databinding.FragmentBuyingBinding

class BuyingFragment : Fragment() {

    private val TAG = "TAG BuyingFragment"

    private lateinit var viewModel: BuyingViewModel

    private var _binding: FragmentBuyingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyingBinding.inflate(inflater, container, false)

        viewModel = (requireActivity() as MainActivity).buyingViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.buyingChats.adapter = ChatsAdapter()

        // To remove the flash when items are updated
        val animator = binding.buyingChats.itemAnimator as SimpleItemAnimator
        animator.supportsChangeAnimations = false

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}