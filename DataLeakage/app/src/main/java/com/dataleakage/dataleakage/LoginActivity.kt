package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.dataleakage.dataleakage.databinding.ActivityLoginBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityLoginBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // removing actionbar
        supportActionBar?.hide()

        binding.loginBtn.setOnClickListener { login() }
    }//end of onCreate

    // login function
    private fun login(){
        // getting email and password values from input
        val email = binding.loginEmailEdt.text.toString()
        val password = binding.loginPassEdt.text.toString()

        // error
        val error = binding.loginError

        // shared preferences object
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        // if input are empty then displaying error
        when {
            email == "" -> {
                error.text = "Email is required!"
            }
            password == "" -> {
                error.text = "Password is required!"
            }
            else -> {
                error.text = ""

                callLoginApi(email, password)
            }
        } // end of when
    }// end of login

    private fun callLoginApi(email:String, password:String){

        val client = OkHttpClient()

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", email)
            .addFormDataPart("password", password)
            .build()

        val url = "${getString(R.string.host_url)}/login"

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request)
            .enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // converting response body in json object
                val jsonObject = JSONObject(response.body?.string())

                // checking if response is ok
                if(response.code == 200){
                    // getting token and isAgent value from json object
                    val authToken = jsonObject.getString("auth_token")
                    val isAgent = jsonObject.getBoolean("isAgent")

                    // opening shared preferences
                    val storage = getSharedPreferences("app", MODE_PRIVATE)

                    // saving the token and isAgent in shared preferences
                    with(storage.edit()){
                        putString("auth_token", "Bearer $authToken")
                        putBoolean("isAgent", isAgent)
                        apply()
                    }

                    // if user is agent the moving to Download Screen
                    if(isAgent){
                        val intent = Intent(this@LoginActivity, DownloadFilesActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    // if user is distributor then moving to Dashboard Screen
                    else{
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                // if the response is not Ok then displaying the error
                else{
                    val error = jsonObject.getString("detail")
                    this@LoginActivity.runOnUiThread(java.lang.Runnable {
                        binding.loginError.text = error.toString()
                    })
                }
            }
        })
    }
}