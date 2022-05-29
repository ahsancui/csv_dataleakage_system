package com.dataleakage.dataleakage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.dataleakage.dataleakage.adaptors.FilesAdaptor
import com.dataleakage.dataleakage.databinding.ActivityDownloadFilesBinding
import com.dataleakage.dataleakage.databinding.ActivitySelectFileBinding
import com.dataleakage.dataleakage.models.FileModel
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class SelectFileActivity : AppCompatActivity() {

    // binding class attribute
    private lateinit var binding: ActivitySelectFileBinding

    private lateinit var files:ArrayList<FileModel>

    private lateinit var adaptor: FilesAdaptor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivitySelectFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Choose File"

        // testing part
        files = ArrayList<FileModel>()
        // recyclerview part
        adaptor = FilesAdaptor(this, files)
        binding.chooseFileList.adapter = adaptor
        binding.chooseFileList.layoutManager = GridLayoutManager(this, 2)

        callFilesApi()

        adaptor.setOnItemClickListener(object:FilesAdaptor.onItemClickListener{
            override fun onItemClick(position: Int) {
                val id = files[position].id
                val name = files[position].name
                val intent = Intent();
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                setResult(RESULT_OK, intent);
                finish();
            }
        })

    }
    private fun callFilesApi(){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        val authToken = storage.getString("auth_token", "").toString()

        val url = "${getString(R.string.host_url)}/files"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", authToken.toString())
            .get()
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    // converting response body in json object
                    val jsonArray = JSONArray(response.body?.string())

                    this@SelectFileActivity.runOnUiThread(java.lang.Runnable {
                        for( i in 0 until jsonArray.length()){
                            val file = jsonArray.getJSONObject(i)

                            val id = file.getInt("id")
                            val name = file.getString("name")

                            files.add(FileModel(id = id, name = name))
                        }
                        adaptor.notifyDataSetChanged()
                    })
                }
            })
    }

}