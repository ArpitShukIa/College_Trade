package com.example.collegetrade.sell.price

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentSetPriceBinding
import com.example.collegetrade.sell.price.PriceState.*
import com.google.android.material.snackbar.Snackbar

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

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.formattedPrice.observe(viewLifecycleOwner, Observer {
            if (it) binding.priceText.setSelection(binding.priceText.text!!.length)
        })

        viewModel.priceState.observe(viewLifecycleOwner, Observer {
            when (it) {
                EMPTY -> showSnackBar("Set a price first...")
                VALID -> binding.adPrice.error = null
                INVALID -> {
                    binding.adPrice.error = "Price must be in range 10 - 9,99,999"
                    showSnackBar("Enter a valid price...")
                }
                NAVIGATE -> navigate()
                else -> {
                }
            }
        })

        return binding.root
    }

    private fun navigate() {
        val adDetails = args.adDetails.plus(viewModel.price.value!!)
        showSnackBar("Navigation started")
    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(binding.btnNext, msg, Snackbar.LENGTH_SHORT).setAnchorView(binding.btnNext)
            .show()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}