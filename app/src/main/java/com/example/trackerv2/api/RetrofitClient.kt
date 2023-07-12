package com.example.trackerv2.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Boolean.FALSE
import java.util.concurrent.TimeUnit

object RetrofitClient {
    val Response.responseCount: Int
        get() = generateSequence(this) { it.priorResponse }.count()

    var authToken = "null"
    var refreshToken = "null"

    private const val BASE_URL = "http://195.238.180.33/api/"
    //private const val BASE_URL = "http://192.168.0.3:80/api/"
    //private const val BASE_URL = "http://192.168.0.171:3000/api/"
    private const val TIME_OUT: Long = 120
    private const val TAG = "RetrofitClient"

    private val gson = GsonBuilder().setLenient().create()
    private var logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("x-access-token", authToken)
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .authenticator(TokenAuthenticator())
        .addInterceptor(logging)
        .build()

    private val okHttpClientNoHeader = OkHttpClient.Builder()
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .followRedirects(FALSE)
        .followSslRedirects(FALSE)
        .build()

    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
        retrofit.create(Api::class.java)
    }

    val instanceRefreshToken: Api by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClientNoHeader)
            .build()
        retrofit.create(Api::class.java)
    }

}
