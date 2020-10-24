package xyz.v.luffy.network


import Base
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BotApi {
    @Headers("Authorization:  Bearer XQVIF6RKSEMNT27JT7Q3NUCQKR5C3EMC")
    @GET("message")
    fun sendMessage(@Query("q") q:String):Call<JsonObject>
}