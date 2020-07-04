package com.arpit.collegetrade.sell.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arpit.collegetrade.R
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment(), View.OnClickListener {

    private val args: CategoryFragmentArgs by navArgs()

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private var doneNavigation = false

    private lateinit var ad: Ad

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        ad = try {
            args.ad.also {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.adFragment, false)
                    .build()
                val directions =
                    CategoryFragmentDirections.actionCategoryFragmentToAdDetailsFlow(it)
                findNavController().navigate(directions, navOptions)
            }
        } catch (e: Exception) {
            Ad()
        }

        doneNavigation = false

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
        if (doneNavigation) return
        doneNavigation = true
        val category = when (v!!.id) {
            R.id.academic_card -> 0
            R.id.vehicle_card -> 1
            R.id.electronics_card -> 2
            R.id.recreational_card -> 3
            else -> 4
        }

        ad.category = "$category"
        val directions =
            if (category < 4)
                CategoryFragmentDirections.actionCategoryFragmentToSubCategoryFragment(ad)
            else
                CategoryFragmentDirections.actionCategoryFragmentToAdDetailsFlow(ad)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
