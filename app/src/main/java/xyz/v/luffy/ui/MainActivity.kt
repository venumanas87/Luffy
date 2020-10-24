package xyz.v.luffy.ui

import android.media.Image
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.v.luffy.R
import xyz.v.luffy.adapters.MessageAdapter
import xyz.v.luffy.models.Chat
import xyz.v.luffy.viewmodel.MessageViewModel

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn:ImageView = findViewById(R.id.send)
        val editText:EditText = findViewById(R.id.edit_query)
        val vm:MessageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,
            RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = layoutManager
        val chatList:ArrayList<Chat> = ArrayList()
      /*  vm.sendMessage("Open WhatsApp messenger").observe(this, Observer {
            Toast.makeText(this,"opening ${it.entities}",Toast.LENGTH_LONG).show()
        })*/
        sendBtn.setOnClickListener {
            val text = editText.text.toString()
            val chat = Chat(true,text)
            chatList.add(chat)
            val adapter = MessageAdapter(chatList,1)
            recyclerView.adapter = adapter
            vm.sendMessage(text).observe(this, Observer {
                Toast.makeText(this,"opening ${it.entities}",Toast.LENGTH_SHORT).show()
            })
        }
    }
}