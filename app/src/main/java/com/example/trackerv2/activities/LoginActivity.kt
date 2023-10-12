package com.example.trackerv2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackerv2.R
import com.example.trackerv2.api.RetrofitClient
import com.example.trackerv2.models.ResponseError
import com.example.trackerv2.models.ResponseLoginDevice
import com.example.trackerv2.storage.UserPreference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.loginButton
import kotlinx.android.synthetic.main.activity_login.loginEditText
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    val userPreference = UserPreference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if(login.isEmpty()) {
                loginEditText.error = getString(R.string.field_cannot_empty)
                loginEditText.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                passwordEditText.error = getString(R.string.field_cannot_empty)
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            RetrofitClient.instance.loginDevice(login, password).enqueue(object: Callback<ResponseLoginDevice> {
                override fun onResponse(
                    call: Call<ResponseLoginDevice>,
                    response: Response<ResponseLoginDevice>
                ) {
                   if(!response.isSuccessful) {
                       val gson = Gson()
                       val responseError: ResponseError = gson.fromJson(
                           response.errorBody()!!.charStream(),
                           ResponseError::class.java
                       )
                       if (responseError.status == 403) {
                           Toast.makeText(this@LoginActivity,
                               R.string.login_password_incorrect, Toast.LENGTH_LONG).show()
                       }
                       Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_LONG).show()

                   } else {
                       runBlocking {
                           userPreference.saveAuthToken(response.body()?.device?.refreshToken.toString())
                       }
                       RetrofitClient.authToken = response.body()?.device?.accessToken.toString()
                       startActivity(intent)
                   }
                }

                override fun onFailure(call: Call<ResponseLoginDevice>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, R.string.failure_connecting, Toast.LENGTH_LONG).show()
                }

            })

        }
    }

}

