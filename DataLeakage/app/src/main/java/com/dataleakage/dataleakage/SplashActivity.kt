package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    // onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // hiding the actionbar from activity
        supportActionBar?.hide()

        // creating the object of shared preferences
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        // adding the delay
        Handler().postDelayed({
            // getting the JSON Web Token from shared preferences
            val authToken = storage.getString("auth_token", "")

            // if no token the moving to Login Screen
            if(authToken == ""){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            // if token exists then login the user
            else{
                // checking if user isAgent or not from shared preferences
                val isAgent = storage.getBoolean("isAgent", true)

                // if user is an Agent the move to Download File screen
                if (isAgent){
                    val intent = Intent(this, DownloadFilesActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                // else display the user Dashboard Screen
                else{
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }// token part ends here
        }, 2000) // time code ends here
    } // onCreate ends here
}