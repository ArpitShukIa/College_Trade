package com.example.collegetrade.sell.price

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.databinding.FragmentSetPriceBinding
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat

class SetPriceFragment : Fragment() {

    private val args: SetPriceFragmentArgs by navArgs()

    private var _binding: FragmentSetPriceBinding? = null
    private val binding get() = _binding!!

    private var formattedString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetPriceBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.priceText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                if (str.isNotEmpty() && str != formattedString) {
                    val num = str.replace(",", "").toInt()
                    formattedString = DecimalFormat("##,##,###").format(num)
                    binding.priceText.setText(formattedString)
                    binding.priceText.setSelection(formattedString.length)
                    binding.adPrice.error =
                        if (num < 10) "Price should have a minimum value of 10." else null
                } else if (str.isEmpty()) {
                    binding.adPrice.error = null
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val s = binding.priceText.text.toString()
            if (s.isBlank()) {
                showSnackBar("Set a price first...")
            } else {
                val price = s.replace(",", "").toInt()
                if (price >= 10) {
                    val adDetails = args.adDetails.plus(s)
                    showSnackBar("Navigation started")
                } else {
                    showSnackBar("Enter a valid price...")
                }
            }
        }

        return binding.root
    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(binding.btnNext, msg, Snackbar.LENGTH_SHORT).setAnchorView(binding.btnNext)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}