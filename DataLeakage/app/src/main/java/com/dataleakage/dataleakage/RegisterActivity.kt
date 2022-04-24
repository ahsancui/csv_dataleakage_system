package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dataleakage.dataleakage.databinding.ActivityDashboardBinding
import com.dataleakage.dataleakage.databinding.ActivityRegisterBinding

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
                //TODO Call api here
            }
        } // end of when
    }// end of registerAgent
}