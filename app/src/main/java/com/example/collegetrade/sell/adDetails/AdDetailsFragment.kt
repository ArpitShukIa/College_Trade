package com.example.collegetrade.sell.adDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.databinding.FragmentAdDetailsBinding
import com.google.android.material.snackbar.Snackbar

class AdDetailsFragment : Fragment() {

    private val args: AdDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentAdDetailsBinding

    private lateinit var adViewModel: AdDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdDetailsBinding.inflate(inflater, container, false)

        adViewModel = ViewModelProvider(this).get(AdDetailsViewModel::class.java)

        binding.apply {
            viewModel = adViewModel
            lifecycleOwner = this@AdDetailsFragment

            topAppBar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            titleText.setOnClickListener {
                showSoftKeyboard(it)
            }

            descText.setOnClickListener {
                showSoftKeyboard(it)
            }

            titleText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showSoftKeyboard(titleText)
                adTitle.error = if (hasFocus) null else adViewModel.titleError.value
            }

            descText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showSoftKeyboard(descText)
                adDescription.error = if (hasFocus) null else adViewModel.descError.value
            }
        }

        observeLiveData()

        return binding.root
    }

    private fun observeLiveData() {
        adViewModel.titleError.observe(viewLifecycleOwner, Observer {
            if (it == null) binding.adTitle.error = null
        })

        adViewModel.descError.observe(viewLifecycleOwner, Observer {
            if (it == null) binding.adDescription.error = null
        })

        adViewModel.isInfoValid.observe(viewLifecycleOwner, Observer { isInfoValid ->
            if (isInfoValid) {
                val adDetails = arrayOf(
                    "${args.catIndex}",
                    "${args.subCatIndex}",
                    adViewModel.title.value!!.trim(),
                    adViewModel.desc.value!!.trim()
                )
                val action =
                    AdDetailsFragmentDirections.actionAdDetailsFragmentToChoosePhotoFragment(
                        adDetails
                    )
                findNavController().navigate(action)
                adViewModel.doneNavigation()
            }
        })

        adViewModel.showSnackBar.observe(viewLifecycleOwner, Observer { showSnackBar ->
            if (showSnackBar) {
                Snackbar.make(
                    binding.btnNext,
                    "Provide a proper title and description first",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.btnNext).show()

                binding.adTitle.error = adViewModel.titleError.value
                binding.adDescription.error = adViewModel.descError.value

                adViewModel.finishSnackBarEvent()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun showSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}