package com.arpit.collegetrade.chats

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.data.MyRoomDatabase
import com.arpit.collegetrade.databinding.FragmentChatBinding
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.hideKeyboard

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()

    private val viewModel: ChatViewModel by viewModels { getViewModelFactory() }

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.setDetails(args.ad, args.chatId, args.otherUserId, args.name, args.image)
        viewModel.messages.observe(viewLifecycleOwner, { })

        binding.toolbar.btnBackLayout.setOnClickListener {
            findNavController().navigateUp()
        }

        manageNotifications()

        return binding.root
    }

    private fun manageNotifications() {
        (requireActivity().application as Application).activeChatId = args.chatId
        NotificationManagerCompat.from(requireContext()).cancelAll()
        val notificationDao = MyRoomDatabase.getDatabase(requireContext()).notificationDao()
        AsyncTask.execute {
            notificationDao.deleteNotificationsByChatId(args.chatId)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(requireActivity(), binding.root)
        (requireActivity().application as Application).activeChatId = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}