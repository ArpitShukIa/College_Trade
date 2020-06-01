package com.example.collegetrade.sell.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.collegetrade.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCategoryBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        return binding.root
    }

}
