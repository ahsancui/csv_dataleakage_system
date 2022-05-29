package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dataleakage.dataleakage.databinding.ActivityDashboardBinding
import com.dataleakage.dataleakage.databinding.ActivityRegisterBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityRegisterBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Register Agent"

        // register button click handler
        binding.registerBtn.setOnClickListener { registerAgent() }

    } //end of onCreate

    // registerAgent function
    private fun registerAgent(){
        val name = binding.registerNameEdt.text.toString()
        val email = binding.registerEmailEdt.text.toString()
        val password = binding.registerPassEdt.text.toString()
        val phone = binding.registerPhoneEdt.text.toString()
        val address = binding.registerAddressEdt.text.toString()

        val error = binding.registerError

        when {
            name == "" -> {
                error.text = "Name is required!"
            }
            email == "" -> {
                error.text = "Email is required!"
            }
            password == "" -> {
                error.text = "Password is required!"
            }
            phone == "" -> {
                error.text = "Phone is required!"
            }
            address == "" -> {
                error.text = "Address is required!"
            }
            else -> {
                error.text = ""
                callRegisterApi(name, email, password, phone, address)
            }
        } // end of when
    }// end of registerAgent

    private fun callRegisterApi(name:String, email:String, password:String, phone:String, address:String){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        val authToken = storage.getString("auth_token", "").toString()

        val jsonObject = JSONObject()
            .put("name",name)
            .put("email", email)
            .put("password", password)
            .put("contact", phone)
            .put("address", address)

        val url = "${getString(R.string.host_url)}/register"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", authToken.toString())
            .post(body)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    // converting response body in json object
                    val jsonObject = JSONObject(response.body?.string())


                    // if the response is not Ok then displaying the error
                    if(response.code == 200 || response.code == 400){
                        val error = jsonObject.getString("detail")
                        this@RegisterActivity.runOnUiThread(java.lang.Runnable {
                            binding.registerError.text = error.toString()
                        })
                    }
                }
            })
    }
}