package xyz.v.luffy.controllers

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import xyz.v.luffy.viewmodel.MessageViewModel

class MessageController(message:String,context: Context) {
    val vm: MessageViewModel = ViewModelProvider(context as ViewModelStoreOwner).get(MessageViewModel::class.java)




}