package com.awesome.weatherapp.di.network

import android.content.Context
import com.awesome.weatherapp.R
import com.awesome.weatherapp.network.GeneralResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class ApiServiceHelperImpl @Inject constructor(
    private val apiService: ApiService,
    private val gsonInstance: Gson,
    private val networkHelper: NetworkHelper,
    @ApplicationContext private val context: Context
) {

    private val internetErr = context.resources.getString(R.string.internetErr)
    private val errorMessage = context.resources.getString(R.string.DecrypError)


    /**
     *
     * @param token String
     * @param endPoint String
     * @param requestJson JSONObject
     * @return String
     */
    suspend fun sendApiRequest(
        token: String,
        endPoint: String,
        requestJson: JSONObject
    ): GeneralResponse {
        try {
            Timber.e("Api request -%s", requestJson)
            if (networkHelper.isNetworkConnected()) {
                val requestBody =
                    requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val response = apiService.makeApiCall(token, endPoint, requestBody)
                when (response.isSuccessful) {
                    true -> {
                        with(response.body().orEmpty()) {
                            val data = this
                            Timber.e("ApiServiceHelperImpl ApiResults response -: %s", this)
                            val responseJson = JSONObject().apply {
                                put("status", response.code())
                                put("data", data)
                                put("message", "isSuccessful")
                            }
                            return gsonInstance.fromJson(
                                responseJson.toString(), GeneralResponse::class.java
                            )
                        }
                    }
                    else -> {
                        Timber.e(
                            "ApiServiceHelperImpl ApiResults errorBody -: %s",
                            response.errorBody().toString()
                        )
                        val responseJson = JSONObject().apply {
                            put("status", response.code())
                            put("data", null)
                            put("message", response.errorBody().toString())
                        }
                        return gsonInstance.fromJson(
                            responseJson.toString(), GeneralResponse::class.java
                        )
                    }
                }
            } else {
                return gsonInstance.fromJson(
                    internetErr, GeneralResponse::class.java
                )
            }
        } catch (ex: Exception) {
            Timber.e("ApiServiceHelperImpl Response Exception%s", ex.message)
            return gsonInstance.fromJson(
                errorMessage, GeneralResponse::class.java
            )
        }
    }
}