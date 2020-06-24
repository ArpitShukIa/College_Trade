package com.example.collegetrade.favorites

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.collegetrade.databinding.FragmentFavoritesBinding
import com.example.collegetrade.home.AdsAdapter
import com.example.collegetrade.util.getViewModelFactory

class FavoritesFragment : Fragment() {

    private val viewModel: HomeFavSharedViewModel by activityViewModels { getViewModelFactory() }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.favoritesList.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    GridLayoutManager(context, 2)
                else
                    GridLayoutManager(context, 4)

            adapter = AdsAdapter(viewModel)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
