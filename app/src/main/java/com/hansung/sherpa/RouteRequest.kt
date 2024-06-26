package com.hansung.sherpa

import TransitService
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class RouteRequest(
    @SerializedName("startX") val startX: String,
    @SerializedName("startY") val startY: String,
    @SerializedName("endX") val endX: String,
    @SerializedName("endY") val endY: String,
    @SerializedName("lang") val lang: Int,
    @SerializedName("format") val format: String,
    @SerializedName("count") val count: Int
)

class TransitManager(context: Context) {

    private val context: Context
    private val retrofit: Retrofit
    private val service: TransitService
    private var routeResponse: RouteResponse = RouteResponse()

    init {
        this.context = context
        retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.route_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(TransitService::class.java)
    }

    fun getRoutes(routeRequest: RouteRequest): LiveData<RouteResponse> {
        val resultLiveData = MutableLiveData<RouteResponse>()
        val appKey = context.getString(R.string.app_key)
        service.getRoutes(appKey, routeRequest).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 실패 처리
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val routeResponse = Klaxon().parse<RouteResponse>(responseBody.string())!!
                        resultLiveData.postValue(routeResponse) // LiveData에 결과 전달
                        Log.d("RESPONSE", "SUCCESS")
                    }
                } else {
                    Log.d("RESPONSE", "ERROR")
                }
            }
        })
        return resultLiveData
    }
}
