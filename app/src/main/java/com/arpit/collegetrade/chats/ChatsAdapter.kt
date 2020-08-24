package com.arpit.collegetrade.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arpit.collegetrade.data.chats.Chat
import com.arpit.collegetrade.databinding.ChatItemLayoutBinding

class ChatsAdapter() :
    ListAdapter<Chat, ChatsAdapter.ViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
    }

    class ViewHolder(val binding: ChatItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.chat = chat
            binding.executePendingBindings()

            binding.chatLayout.setOnClickListener {
                val directions = AllChatsFragmentDirections.actionAllChatsFragmentToChatFragment(
                    chat.ad, chat.id, chat.personId, chat.name, chat.personImage
                )
                binding.root.findNavController().navigate(directions)
            }
        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }
}