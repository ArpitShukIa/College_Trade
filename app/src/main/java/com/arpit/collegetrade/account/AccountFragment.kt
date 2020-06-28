package com.arpit.collegetrade.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.arpit.collegetrade.databinding.FragmentAccountBinding
import com.arpit.collegetrade.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment(), OnClickListener {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.appBarLayout.outlineProvider = null

        binding.myAdsLayout.setOnClickListener(this)
        binding.inviteFriendLayout.setOnClickListener(this)
        binding.rateUsLayout.setOnClickListener(this)
        binding.logoutLayout.setOnClickListener(this)

        binding.topAppBar.setOnMenuItemClickListener {
            showToast(requireContext(), "About")
            true
        }

        return binding.root
    }

    private fun navigateToMyAds() {
        val directions = AccountFragmentDirections.actionAccountFragmentToMyAdsFragment()
        findNavController().navigate(directions)
    }

    private fun sendInviteLink() {
        // TODO Change redirect url to College Trade link
        val text = getString(R.string.invite_msg)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun openPlayStore() {
        // TODO Replace the uri with College Trade link
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.tencent.iglite")
            setPackage("com.android.vending")
        }
        startActivity(intent)
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

    override fun onClick(v: View?) {
        when (v) {
            binding.myAdsLayout -> navigateToMyAds()
            binding.inviteFriendLayout -> sendInviteLink()
            binding.rateUsLayout -> openPlayStore()
            binding.logoutLayout -> handleLogout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
