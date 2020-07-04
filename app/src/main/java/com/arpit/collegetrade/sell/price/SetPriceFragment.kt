package com.arpit.collegetrade.sell.price

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.arpit.collegetrade.EventObserver
import com.arpit.collegetrade.R
import com.arpit.collegetrade.databinding.FragmentSetPriceBinding
import com.arpit.collegetrade.sell.price.PriceState.*
import com.arpit.collegetrade.util.adjustResize
import com.arpit.collegetrade.util.hideKeyboard
import com.arpit.collegetrade.util.showSnackBar

class SetPriceFragment : Fragment() {

    private val args: SetPriceFragmentArgs by navArgs()

    private var _binding: FragmentSetPriceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SetPriceViewModel by navGraphViewModels(R.id.adDetailsFlow)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetPriceBinding.inflate(inflater, container, false)
        adjustResize(requireActivity(), true)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setPrice(args.ad.price)

        viewModel.formattedPrice.observe(viewLifecycleOwner, Observer {
            if (it) binding.priceText.setSelection(binding.priceText.text!!.length)
        })

        viewModel.priceState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                EMPTY -> showSnackBar(binding.btnNext, "Set a price first...")
                VALID -> binding.adPrice.error = null
                INVALID -> {
                    binding.adPrice.error = "Price must be in range 10 - 9,99,999"
                    showSnackBar(binding.btnNext, "Enter a valid price...")
                }
                NAVIGATE -> navigate()
            }
        })

        return binding.root
    }

    private fun navigate() {
        val ad = args.ad
        ad.price = viewModel.price.value!!
        val directions =
            SetPriceFragmentDirections.actionSetPriceFragmentToReviewDetailsFragment(ad)
        findNavController().navigate(directions)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(requireActivity(), binding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}