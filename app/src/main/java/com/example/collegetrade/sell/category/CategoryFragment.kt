package com.example.collegetrade.sell.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCategoryBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
        
        binding.apply {
            academicCard.setOnClickListener(this@CategoryFragment)
            vehicleCard.setOnClickListener(this@CategoryFragment)
            electronicsCard.setOnClickListener(this@CategoryFragment)
            recreationalCard.setOnClickListener(this@CategoryFragment)
            othersCard.setOnClickListener(this@CategoryFragment)
        }

        return binding.root
    }

    override fun onClick(v: View?) {
        val pos = when (v!!.id) {
            R.id.academic_card -> 0
            R.id.vehicle_card -> 1
            R.id.electronics_card -> 2
            R.id.recreational_card -> 3
            else -> 4
        }
        if (pos < 4) {
            val action = CategoryFragmentDirections.actionCategoryFragmentToSubCategoryFragment(pos)
            findNavController().navigate(action)
        }
    }

}
