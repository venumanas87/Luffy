package xyz.v.luffy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import xyz.v.luffy.viewmodel.MessageViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vm:MessageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        vm.sendMessage("Open WhatsApp messenger").observe(this, Observer {
            Toast.makeText(this,"opening ${it.entities}",Toast.LENGTH_LONG).show()
        })

    }
}