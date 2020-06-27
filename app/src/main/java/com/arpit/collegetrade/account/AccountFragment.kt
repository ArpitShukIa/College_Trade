package com.arpit.collegetrade.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.arpit.collegetrade.databinding.FragmentAccountBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.logoutLayout.setOnClickListener(this)
        binding.rateUsLayout.setOnClickListener(this)

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

    private fun openPlayStore() {
        // TODO Replace the uri with College Trade link
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.tencent.iglite")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.logoutLayout -> handleLogout()
            binding.rateUsLayout -> openPlayStore()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
