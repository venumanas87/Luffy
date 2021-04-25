package xyz.v.luffy.viewmodel

import Base
import Intents
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.v.luffy.network.BotApi
import java.io.IOException

class MessageViewModel:ViewModel() {
    var bse:MutableLiveData<Base> = MutableLiveData()
    var mesage:String=""
    fun setmessage(message:String){
        this.mesage = message
        sendMessage()
    }

    fun sendMessage():LiveData<Base>{
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit =  Retrofit.Builder()
            .baseUrl("https://api.wit.ai/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BotApi::class.java)
        val call = service.sendMessage(mesage)
        call.enqueue(object : retrofit2.Callback<JsonObject> {

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }

            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: retrofit2.Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    val mainObj = JSONObject(Gson().toJson(response.body()))
                    val intentList: ArrayList<Intents> = ArrayList()
                    val intensArr = mainObj.getJSONArray("intents")
                    if (intensArr.length()>0) {
                        val intentName = intensArr.getJSONObject(0).getString("name")
                        intentList.add(Intents(intentName))
                        val entityObj = mainObj.getJSONObject("entities")
                        when (intentName) {
                            "open_app" -> {
                                if (entityObj.length() > 0) {
                                    val appnameArr = entityObj.getJSONArray("app_name:app_name")
                                    val entityName = appnameArr.getJSONObject(0).getString("body")?:""
                                    val base =
                                        Base(mainObj.getString("text"), intentList, entityName)
                                    bse.postValue(base)
                                } else {
                                    intentList.clear()
                                    intentList.add(Intents("default"))
                                    val base = Base(
                                        mainObj.getString("text"),
                                        intentList,
                                        ""
                                    )
                                    bse.postValue(base)
                                }
                            }
                            "play_song" -> {
                                if (entityObj.length() > 0) {
                                    val songnameArr = entityObj.getJSONArray("song_name:song_name")
                                    val entityName = songnameArr.getJSONObject(0).getString("body")
                                    val base =
                                        Base(mainObj.getString("text"), intentList, entityName)
                                    bse.postValue(base)
                                } else {
                                    intentList.add(Intents("default"))
                                    val base = Base(
                                        mainObj.getString("text"),
                                        intentList,
                                        ""
                                    )
                                    bse.postValue(base)
                                }
                            }
                            "incognito_start" -> {
                                val base = Base(
                                    mainObj.getString("text"),
                                    intentList,
                                    ""
                                )
                                bse.postValue(base)
                            }
                            "get_time" -> {
                                val base = Base(mainObj.getString("text"), intentList, "")
                                bse.postValue(base)
                            }
                            "initiate" -> {
                                val base = Base(mainObj.getString("text"), intentList, "")
                                bse.postValue(base)
                            }


                        }

                        Log.d("TAG", "onResponse: ${mainObj.getString("intents")}")


                        /*val json = base.entities
                    val obj = JSONObject(json)
                    if (base.intents[0].name == "open_app") {
                        val mainentarr = obj.getJSONArray("app_name:app_name")
                        val appnameObj = mainentarr.getJSONObject(0)
                        val appName = appnameObj.get("body")
                        Log.d("TAG", "onResponse: $appName")*/
                    }else {
                        intentList.add(Intents("default"))
                        val base = Base(
                            mainObj.getString("text"),
                            intentList,
                            ""
                        )
                        bse.postValue(base)
                    }
                }
            }


        })
  return bse
    }
}