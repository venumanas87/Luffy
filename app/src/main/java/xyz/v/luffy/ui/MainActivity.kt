package xyz.v.luffy.ui

import Base
import android.animation.Animator
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
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
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xyz.v.luffy.R
import xyz.v.luffy.constants.StringReplies
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
    val sr = StringReplies
    var isIncognito = false
    var toolbar:RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn:ImageView = findViewById(R.id.send)
        val editText:EditText = findViewById(R.id.edit_query)
        val vm:MessageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        toolbar = findViewById(R.id.toolbar)
        typing = findViewById(R.id.typing)
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorPrimaryDarker)
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
            val chat = Chat(1, text)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
            vm.setmessage(text)
            editText.setText("")
            }else{
                sendfromBot(sr.TYPE_VALID)
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
                    sendfromBot(sr.INIT_TEXT)
                }
                "incognito_start" -> {
                    if(!isIncognito) {
                        incognitomode()
                        sendfromBot(sr.INCOGNITO_MODE)
                        revealIcognito()
                    }else{
                        publicmode()
                        sendfromBot(sr.INCOGNITO_MODE_OFF)
                        reveal()
                    }
                }
                "get_time" ->{
                    showTime()
                }
                "default" -> {
                    sendfromBot(sr.DEFAULT)
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
            val chat = Chat(2, messg)
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
    companion object{
        val TAG = "MAin"
    }

    fun reveal(){
        val view:View = findViewById(R.id.bgCircle)
        val view1:View = findViewById(R.id.bgCircleInc)
        view1.visibility = View.GONE
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

    fun revealIcognito(){
        val view:View = findViewById(R.id.bgCircleInc)
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
    fun incognitomode(){
        isIncognito = true
        window.statusBarColor = ContextCompat.getColor(this,R.color.blackDark)
        toolbar?.background?.setTint(ContextCompat.getColor(this,R.color.blackDark))
    }
    fun publicmode(){
        isIncognito = false
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorPrimaryDarker)
        toolbar?.background?.setTint(ContextCompat.getColor(this,R.color.colorPrimaryDarker))
    }
    fun showTime(){
        typing?.visibility = View.GONE
        val chat = Chat(3,"")
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        chatList.add(chat)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(chatList.lastIndex)
    }
}