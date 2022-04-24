package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UploadFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)

        // setting new title on action bar
        supportActionBar?.title = "Upload File"

    }
}