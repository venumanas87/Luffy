package xyz.v.luffy.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
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
import Base
import android.animation.StateListAnimator
import android.os.*
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.*
import androidx.dynamicanimation.animation.SpringForce
import androidx.transition.TransitionManager
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val chatList:ArrayList<Chat> = ArrayList()
    val adapter = MessageAdapter(chatList)
    var typing:LottieAnimationView? = null
    var c = 0
    val sr = StringReplies
    var isIncognito = false
    var toolbar:RelativeLayout? = null
    lateinit var iv:RiveAnimationView
    lateinit var close:ImageView
    lateinit var speechRecognizer:SpeechRecognizer
    lateinit var box:RelativeLayout
    lateinit var boxcont:RelativeLayout
    lateinit var fab:FloatingActionButton
    lateinit var stttv:TextView
    lateinit var rive:RiveAnimationView
    lateinit var vm:MessageViewModel
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn:ImageView = findViewById(R.id.send)
        val editText:EditText = findViewById(R.id.edit_query)
        vm = ViewModelProvider(this).get(MessageViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        box = findViewById(R.id.cardFab)
        boxcont = findViewById(R.id.boxcont)
        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar)
        typing = findViewById(R.id.typing)
        stttv = findViewById(R.id.stt)
        iv = findViewById(R.id.icon)
        close = findViewById(R.id.close)
        rive = findViewById(R.id.rive)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        init()

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



        iv.setOnClickListener {
            iv.play(Loop.ONESHOT)
        }

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

        fab.setOnClickListener {
            setUpSpring()
        }

        close.setOnClickListener {
            restorefab(it)
        }
        keyboard.setOnClickListener {
            boxcont.visibility = View.VISIBLE
            it.visibility = View.GONE

            SpringAnimation(fab, DynamicAnimation.TRANSLATION_Y, -150f).start()


        }


    }

    fun restorefab(view:View) {

        iv.reset()
        speechRecognizer.stopListening()
        speechRecognizer.cancel()

        boxcont.visibility = View.GONE
        keyboard.visibility = View.VISIBLE

        val transform = MaterialContainerTransform().apply {
            // Manually tell the container transform which Views to transform between.
            startView = box
            endView = fab

            // Ensure the container transform only runs on a single target
            addTarget(fab)
            scrimColor = Color.TRANSPARENT

            // Since View to View transforms often are not transforming into full screens,

        }
        transform.setPathMotion(MaterialArcMotion())

        TransitionManager.beginDelayedTransition(fab.parent as ViewGroup, transform)
        fab.visibility = View.VISIBLE
        box.visibility = View.GONE


        SpringAnimation(fab, DynamicAnimation.TRANSLATION_Y, 0f).start()

    }

    private fun init() {

        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {

            }

            override fun onError(error: Int) {

            }

            override fun onResults(results: Bundle?) {
                val data: java.util.ArrayList<String>? =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val tt = data?.get(0).toString()
                stttv.text = tt
                Handler(Looper.getMainLooper()).postDelayed({
                    rive.setRiveResource(R.raw.complt)
                    rive.play(loop = Loop.ONESHOT)
                    rive.fit = Fit.FILL
                    val chat = Chat(1, tt)
                    chatList.add(chat)
                    adapter.notifyDataSetChanged()
                    vm.setmessage(tt)
                },2000)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                TODO("Not yet implemented")
            }

        })
    }





    private fun setUpSpring() {

        iv.play("Animation 2")


        val transform = MaterialContainerTransform().apply {
            // Manually tell the container transform which Views to transform between.
            startView = fab
            endView = box

            // Ensure the container transform only runs on a single target
            addTarget(box)
            scrimColor = Color.TRANSPARENT

            // Since View to View transforms often are not transforming into full screens,

        }

        stttv.text = "Listening..."
        rive.setRiveResource(R.raw.listening)
        rive.play(Loop.LOOP)
        rive.fit = Fit.FILL

        transform.setPathMotion(MaterialArcMotion())
        transform.duration = 300
        transform.containerColor = Color.WHITE
        transform.startContainerColor = Color.WHITE
        transform.endContainerColor = Color.WHITE
        transform.setAllContainerColors(Color.WHITE)
        val intentt = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intentt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        SpringAnimation(fab, DynamicAnimation.TRANSLATION_Y, -300f).addEndListener { animation, canceled, value, velocity ->
            TransitionManager.beginDelayedTransition(fab.parent as ViewGroup, transform)
            fab.visibility = View.GONE
            box.visibility = View.VISIBLE
            speechRecognizer.startListening(intentt)
        }.start()
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
            typing?.visibility = View.GONE
            val chat = Chat(2, messg)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(chatList.lastIndex)
        }
    }
    private fun launchApp(packageName: String) {
        val pm = applicationContext.packageManager
        val intent:Intent? = pm.getLaunchIntentForPackage(packageName)
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
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

//    fun reveal(){
//        val view:View = findViewById(R.id.bgCircle)
//        val view1:View = findViewById(R.id.bgCircleInc)
//        view1.visibility = View.GONE
//        val cy = view.height/2
//        val cx = view.width/2
//        val finalrad: Double = Math.hypot(cx.toDouble(), cy.toDouble())
//        val anim:Animator = ViewAnimationUtils.createCircularReveal(view,cx,cy, 0F,
//            finalrad.toFloat()
//        )
//        val vib:Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            vib.vibrate(VibrationEffect.createOneShot(20,VibrationEffect.DEFAULT_AMPLITUDE))
//        }else{
//            vib.vibrate(20)
//        }
//        view.visibility = View.VISIBLE
//        anim.start()
//    }

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


    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}