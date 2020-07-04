package com.arpit.collegetrade.home.ad

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arpit.collegetrade.R
import com.arpit.collegetrade.databinding.FragmentZoomPhotoBinding
import com.bumptech.glide.Glide

class ZoomPhotoFragment : Fragment() {

    private val args: ZoomPhotoFragmentArgs by navArgs()

    private var _binding: FragmentZoomPhotoBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentZoomPhotoBinding.inflate(inflater, container, false)

        window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        Glide.with(this)
            .load(args.uri)
            .into(binding.zoomImageView)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        window.statusBarColor = Color.BLACK
    }

    override fun onPause() {
        super.onPause()
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}