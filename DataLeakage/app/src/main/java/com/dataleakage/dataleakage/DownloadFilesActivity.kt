package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.dataleakage.dataleakage.adaptors.FilesAdaptor
import com.dataleakage.dataleakage.databinding.ActivityDownloadFilesBinding
import com.dataleakage.dataleakage.models.FileModel

class DownloadFilesActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityDownloadFilesBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityDownloadFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Download Files"

        // testing part
        val files = ArrayList<FileModel>()
        files.add(FileModel(id = 1, name = "file1.csv"))
        files.add(FileModel(id = 2, name = "file2.csv"))
        files.add(FileModel(id = 3, name = "file3.csv"))
        files.add(FileModel(id = 4, name = "file4.csv"))

        // recyclerview part
        val adaptor = FilesAdaptor(this, files)
        binding.filesRecyclerview.adapter = adaptor
        binding.filesRecyclerview.layoutManager = GridLayoutManager(this, 2)

    }// end of onCreate
}