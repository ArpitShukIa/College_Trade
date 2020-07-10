package com.arpit.collegetrade.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arpit.collegetrade.chats.buy.BuyingFragment
import com.arpit.collegetrade.chats.sell.SellingFragment
import com.arpit.collegetrade.databinding.FragmentChatsBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_bar_layout.view.*

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        binding.toolbar.drawer_icon.setOnClickListener {
            requireActivity().drawer.openDrawer(GravityCompat.START)
        }

        binding.viewPager.adapter = TabsAdapter(this)

        TabLayoutMediator(binding.chatTabs, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "BUYING" else "SELLING"
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) =
        if (position == 0) BuyingFragment() else SellingFragment()
}