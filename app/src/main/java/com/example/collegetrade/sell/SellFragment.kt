package com.example.collegetrade.sell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.collegetrade.R

class SellFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sell, container, false)

        startActivity(Intent(activity,SellActivity::class.java))

        findNavController().navigate(R.id.homeFragment)

        return view
    }

}
