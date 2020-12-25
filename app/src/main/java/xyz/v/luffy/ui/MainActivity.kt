package xyz.v.luffy.ui

import Base
import android.animation.Animator
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xyz.v.luffy.R
import xyz.v.luffy.adapters.MessageAdapter
import xyz.v.luffy.models.Chat
import xyz.v.luffy.viewmodel.MessageViewModel
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val chatList:ArrayList<Chat> = ArrayList()
    val adapter = MessageAdapter(chatList)
    var typing:LottieAnimationView? = null
    var c = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn:ImageView = findViewById(R.id.send)
        val editText:EditText = findViewById(R.id.edit_query)
        val vm:MessageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        typing = findViewById(R.id.typing)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        vm.sendMessage().observe(this, {
            handleMessage(it)
        })

        typing?.visibility = View.VISIBLE
        sendBtn.isEnabled = false
        vm.setmessage("welcome")
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    sendBtn.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        sendBtn.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()){
            typing?.visibility = View.VISIBLE
            val chat = Chat(true, text)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
            vm.setmessage(text)
            editText.setText("")
            }else{
                sendfromBot("Type a valid message da!")
            }

        }
    }

    private fun handleMessage(base: Base) {
        if (!base.intents.isEmpty()) {
            when (base.intents[0].name) {
                "open_app" -> {
                    CoroutineScope(IO).launch {
                        apps(base.entities)
                    }
                }
                "play_song" -> {
                    sendfromBot("Playing ${base.entities}")
                    launchYoutube(base.entities)
                }
                "initiate" -> {
                    sendfromBot("Hello, i am luffy your personal AI bot.You can command me to do some tasks like Play some song or Open app")
                }
                "default" -> {
                    sendfromBot("Sorry , i am still in training period to understand your message")
                }
            }
        }
    }

    private fun sendfromBot(messg: String) {
        c++
        runOnUiThread {
            if (c<2){
                reveal()
            }
            typing?.visibility = View.GONE
            val chat = Chat(false, messg)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(chatList.lastIndex)
        }
    }
    private fun launchApp(packageName: String) {
        // Get an instance of PackageManager
        val pm = applicationContext.packageManager

        // Initialize a new Intent
        val intent:Intent? = pm.getLaunchIntentForPackage(packageName)

        // Add category to intent
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)

        // If intent is not null then launch the app
        if(intent!=null){
            applicationContext.startActivity(intent)
        }else{
            Log.d("AG", "launchApp: no app")
        }
    }

    private fun apps(appName: String) {
        val pm = packageManager
//get a list of installed apps.
//get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var Noopen:Boolean = true
        for (packageInfo in packages) {

            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {

                if (packageInfo.loadLabel(packageManager).toString().toLowerCase(Locale.ROOT) == appName){
                    sendfromBot("Opening ${appName.toUpperCase(Locale.ROOT)}")
                    launchApp(packageInfo.packageName)
                    Noopen=false
                }
            }
        }
        if (Noopen){
            sendfromBot("No apps")
        }
    }
    private fun launchYoutube(query: String) {
        // Get an instance of PackageManager
        val pm = applicationContext.packageManager

        // Initialize a new Intent
        val intent:Intent? = Intent(Intent.ACTION_SEARCH)
         intent?.setPackage("com.google.android.youtube")
        intent?.putExtra("query", query)
        // Add category to inten
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // If intent is not null then launch the app
        if(intent!=null){
            applicationContext.startActivity(intent)
        }else{
            Log.d("AG", "launchApp: no app")
        }
    }
    private fun openWhatsApp() {
        val smsNumber = "919131244817" // E164 format without '+' sign
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        sendIntent.putExtra("jid", "$smsNumber@s.whatsapp.net") //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp")
        if (intent.resolveActivity(this.packageManager) == null) {
            Toast.makeText(this, "Error/n", Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(sendIntent)
    }
    companion object{
        val TAG = "MAin"
    }

    fun reveal(){
        val view:View = findViewById(R.id.bgCircle)
        val cy = view.height/2
        val cx = view.width/2
        val finalrad: Double = Math.hypot(cx.toDouble(), cy.toDouble())
        val anim:Animator = ViewAnimationUtils.createCircularReveal(view,cx,cy, 0F,
            finalrad.toFloat()
        )
        val vib:Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(20,VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            vib.vibrate(20)
        }
        view.visibility = View.VISIBLE
        anim.start()
    }

}