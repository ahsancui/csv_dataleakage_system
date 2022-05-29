package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    // adding log out menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    // handling on click on the menu logout button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> {
                val storage = getSharedPreferences("app", MODE_PRIVATE)
                with(storage.edit()){
                    remove("auth_token")
                    remove("isAgent")
                    apply()
                }
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
// menu ends

} // class ends