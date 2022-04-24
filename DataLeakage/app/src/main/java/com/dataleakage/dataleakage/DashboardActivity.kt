package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dataleakage.dataleakage.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding:ActivityDashboardBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Dashboard"

        // opening agents activity/screen
        binding.allAgentsBtn.setOnClickListener {
            val intent = Intent(this, AgentsActivity::class.java)
            startActivity(intent)
        }

        // opening register agent activity/screen
        binding.newAgentBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // opening download files activity/screen
        binding.allFilesBtn.setOnClickListener {
            val intent = Intent(this, DownloadFilesActivity::class.java)
            startActivity(intent)
        }

        // opening new file upload activity/screen
        binding.newFileBtn.setOnClickListener {
            val intent = Intent(this, UploadFileActivity::class.java)
            startActivity(intent)
        }

        // opening find guilty agent activity/screen
        binding.findGuiltyBtn.setOnClickListener {
            val intent = Intent(this, FindGuiltyActivity::class.java)
            startActivity(intent)
        }

    } // onCreate ends
} // class ends