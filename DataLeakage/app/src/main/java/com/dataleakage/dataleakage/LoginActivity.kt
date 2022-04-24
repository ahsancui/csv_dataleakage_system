package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dataleakage.dataleakage.databinding.ActivityLoginBinding
import com.dataleakage.dataleakage.databinding.ActivityRegisterBinding

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
        val email = binding.loginEmailEdt.text.toString()
        val password = binding.loginPassEdt.text.toString()

        val error = binding.loginError

        when {
            email == "" -> {
                error.text = "Email is required!"
            }
            password == "" -> {
                error.text = "Password is required!"
            }
            else -> {
                error.text = ""
                //TODO Call api here

                val isDistributor = email == "1"

                if(isDistributor){
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    val intent = Intent(this, DownloadFilesActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        } // end of when
    }// end of login
}