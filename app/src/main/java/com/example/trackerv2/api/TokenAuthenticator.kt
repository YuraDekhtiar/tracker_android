package com.example.trackerv2.api

import com.example.trackerv2.api.RetrofitClient.responseCount
import com.example.trackerv2.models.ResponseRefreshToken
import com.example.trackerv2.storage.UserPreference
import com.example.trackerv2.utils.ContextHolder
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback

class TokenAuthenticator() : Authenticator {
    val userPreference = UserPreference(ContextHolder.context)

    override fun authenticate(route: Route?, response: Response): Request? {
        if(response.responseCount > 1)
            return null
        getUpdatedToken()
        return response.request.newBuilder()
            .header("x-access-token", RetrofitClient.authToken)
            .build()
    }

    private fun getUpdatedToken() {
        RetrofitClient.instanceRefreshToken.refreshToken(RetrofitClient.refreshToken).enqueue(object:
            Callback<ResponseRefreshToken> {
            override fun onResponse(
                call: Call<ResponseRefreshToken>,
                response: retrofit2.Response<ResponseRefreshToken>
            ) {
                if(response.isSuccessful) {
                    RetrofitClient.authToken = response.body()?.result?.accessToken.toString()
                    RetrofitClient.refreshToken = response.body()?.result?.refreshToken.toString()

                    runBlocking {
                        userPreference.saveAuthToken(response.body()?.result?.refreshToken.toString())
                    }
                }
            }
            override fun onFailure(call: Call<ResponseRefreshToken>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


}

