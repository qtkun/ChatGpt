package com.qtkun.chatgpt.ext

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.qtkun.chatgpt.R

private var View.lastClickTime: Long
    get() = if (getTag(R.id.last_click_time) != null) getTag(R.id.last_click_time) as Long else 0L
    set(value) {
        setTag(R.id.last_click_time, value)
    }

fun View.singleClick(duration: Long = 500L, onClick: (View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        if(currentClickTime - lastClickTime > duration) {
            onClick(it)
        }
        lastClickTime = currentClickTime
    }
}

fun View.hideKeyboard() {
    context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    context.getSystemService(InputMethodManager::class.java)?.showSoftInput(this, 0)
}

fun TextView.setSelectionMenu(textMenuItemOnClickListener: TextMenuItemOnClickListener) {
    customSelectionActionModeCallback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.let {
                for(i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId == android.R.id.shareText) {
                        menu.removeItem(item.itemId)
                        break
                    }
                }
                menu.add(Menu.NONE, R.id.message_delete, 101, "删除")
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.message_delete -> {
                    textMenuItemOnClickListener.onMessageDelete()
                    mode?.finish()
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}

interface TextMenuItemOnClickListener {
    fun onMessageDelete()
}