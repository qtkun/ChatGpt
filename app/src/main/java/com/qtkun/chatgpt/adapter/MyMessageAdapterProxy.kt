package com.qtkun.chatgpt.adapter

import com.qtkun.chatgpt.base.AdapterProxy
import com.qtkun.chatgpt.base.BaseViewHolder
import com.qtkun.chatgpt.bean.UserMessageBean
import com.qtkun.chatgpt.databinding.ItemMyMessageBinding
import com.qtkun.chatgpt.ext.TextMenuItemOnClickListener
import com.qtkun.chatgpt.ext.setSelectionMenu


class MyMessageAdapterProxy(private val onMessageDelete: (Int) -> Unit): AdapterProxy<UserMessageBean, ItemMyMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemMyMessageBinding>,
        item: UserMessageBean,
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

    override fun areItemsTheSame(oldItem: UserMessageBean, newItem: UserMessageBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserMessageBean, newItem: UserMessageBean): Boolean {
        return oldItem.content == newItem.content
    }
}