package com.arpit.collegetrade.chats

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.R
import com.arpit.collegetrade.chats.buy.BuyingFragment
import com.arpit.collegetrade.chats.sell.SellingFragment
import com.arpit.collegetrade.data.MyRoomDatabase
import com.arpit.collegetrade.databinding.FragmentAllChatsBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_bar_layout.view.*

class AllChatsFragment : Fragment() {

    private var _binding: FragmentAllChatsBinding? = null
    private val binding get() = _binding!!

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllChatsBinding.inflate(inflater, container, false)

        val notificationDao = MyRoomDatabase.getDatabase(requireActivity()).notificationDao()
        AsyncTask.execute {
            notificationDao.deleteAllNotifications()
        }

        binding.toolbar.drawer_icon.setOnClickListener {
            requireActivity().drawer.openDrawer(GravityCompat.START)
        }

        binding.viewPager.adapter = TabsAdapter(this)

        tabLayoutMediator =
            TabLayoutMediator(binding.chatTabs, binding.viewPager) { tab, position ->
                tab.text = if (position == 0) "BUYING" else "SELLING"
            }
        tabLayoutMediator?.attach()

        trackUnreadCounts()

        return binding.root
    }

    private fun trackUnreadCounts() {
        val app = requireActivity().application as Application
        app.buyUnreadCount.observe(viewLifecycleOwner, { count ->
            if (count == 0)
                binding.chatTabs.getTabAt(0)!!.removeBadge()
            else {
                val badge = binding.chatTabs.getTabAt(0)!!.orCreateBadge
                badge.number = count
                badge.backgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            }
        })

        app.sellUnreadCount.observe(viewLifecycleOwner, { count ->
            if (count == 0)
                binding.chatTabs.getTabAt(1)!!.removeBadge()
            else {
                val badge = binding.chatTabs.getTabAt(1)!!.orCreateBadge
                badge.number = count
                badge.backgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        binding.viewPager.adapter = null
        tabLayoutMediator = null
        _binding = null
    }
}

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) =
        if (position == 0) BuyingFragment() else SellingFragment()
}