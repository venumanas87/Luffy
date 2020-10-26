package xyz.v.luffy.ui

import Base
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import xyz.v.luffy.R
import xyz.v.luffy.adapters.MessageAdapter
import xyz.v.luffy.models.Chat
import xyz.v.luffy.viewmodel.MessageViewModel

class MainActivity : AppCompatActivity() {
    val chatList:ArrayList<Chat> = ArrayList()
    val adapter = MessageAdapter(chatList)
    var typing:LottieAnimationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn:ImageView = findViewById(R.id.send)
        val editText:EditText = findViewById(R.id.edit_query)
        val vm:MessageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        typing = findViewById(R.id.typing)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,
            RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        vm.sendMessage().observe(this, {
              typing?.visibility = View.GONE
            handleMessage(it)
        })
        typing?.visibility = View.VISIBLE
        sendBtn.isEnabled = false
        vm.setmessage("welcome")
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             if (p0!!.isNotEmpty()){
                 sendBtn.isEnabled = true
             }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        sendBtn.setOnClickListener {
            typing?.visibility = View.VISIBLE
            val text = editText.text.toString()
            val chat = Chat(true,text)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
            vm.setmessage(text)

        }
    }

    private fun handleMessage(base: Base) {
        if (!base.intents.isEmpty()) {
            when (base.intents[0].name) {
                "open_app" -> {
                    sendfromBot("Opening ${base.entities}")
                }
                "play_song"->{
                    sendfromBot("Playing ${base.entities}")
                }
                "initiate"->{
                    sendfromBot("Hello, i am luffy your personal AI bot.You can command me to do some tasks like Play some song or Open app")
                }
                "default"->{
                    sendfromBot("Sorry , i am still in training period to understand your message")
                }
            }
        }
    }

    private fun sendfromBot(messg: String) {
        val chat = Chat(false,messg)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        chatList.add(chat)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(chatList.lastIndex)
    }
}