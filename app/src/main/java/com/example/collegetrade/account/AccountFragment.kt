package com.example.collegetrade.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.example.collegetrade.R
import com.example.collegetrade.SplashScreenActivity
import com.example.collegetrade.databinding.FragmentAccountBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.logoutLayout.setOnClickListener {
            handleLogout()
        }

        return binding.root
    }

    private fun handleLogout() {
        MaterialDialog(requireContext())
            .show {
                title(R.string.are_you_sure)
                positiveButton(R.string.logout) {
                    Firebase.auth.signOut()
                    startActivity(Intent(activity, SplashScreenActivity::class.java))
                    requireActivity().finish()
                }
                negativeButton(R.string.cancel)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
