package com.qtkun.chatgpt.adapter

import com.qtkun.chatgpt.base.AdapterProxy
import com.qtkun.chatgpt.base.BaseViewHolder
import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.databinding.ItemChatgptMessageBinding
import com.qtkun.chatgpt.ext.TextMenuItemOnClickListener
import com.qtkun.chatgpt.ext.setSelectionMenu


class ChatGPTAdapterProxy(private val onMessageDelete: (Int) -> Unit): AdapterProxy<ChatMessageBean, ItemChatgptMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemChatgptMessageBinding>,
        item: ChatMessageBean,
        position: Int
    ) {
        with(holder.binding) {
            tvContent.text = item.content
            tvContent.setSelectionMenu(object: TextMenuItemOnClickListener {
                override fun onMessageDelete() {
                    onMessageDelete(holder.adapterPosition)
                }
            })
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemChatgptMessageBinding>,
        item: ChatMessageBean,
        position: Int,
        payloads: MutableList<Any>
    ) {
        for (payload in payloads) {
            if (payload is String) {
                item.content = payload
                holder.binding.tvContent.text = item.content
            }
        }
    }

    override fun areItemsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }
}