package com.dataleakage.dataleakage

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.GridLayoutManager
import com.dataleakage.dataleakage.adaptors.FilesAdaptor
import com.dataleakage.dataleakage.databinding.ActivityDownloadFilesBinding
import com.dataleakage.dataleakage.models.FileModel
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class DownloadFilesActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityDownloadFilesBinding

    private lateinit var files:ArrayList<FileModel>

    private lateinit var adaptor:FilesAdaptor

    private var download_id:Long = 0
    private  var file_name = "Data leakage download"

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityDownloadFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Download Files"

        // testing part
        files = ArrayList<FileModel>()
        // recyclerview part
        adaptor = FilesAdaptor(this, files)
        binding.filesRecyclerview.adapter = adaptor
        binding.filesRecyclerview.layoutManager = GridLayoutManager(this, 2)

        callFilesApi()

        adaptor.setOnItemClickListener(object:FilesAdaptor.onItemClickListener{
            override fun onItemClick(position: Int) {
                val id = files[position].id

                val input = EditText(this@DownloadFilesActivity)
                input.setText(file_name)

                input.inputType = InputType.TYPE_CLASS_TEXT


                val dialog = AlertDialog.Builder(this@DownloadFilesActivity)
                    .setTitle("Download File")
                    .setView(input)
                    .setPositiveButton("Download",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            file_name = input.text.toString()
                            downloadFile(id)
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                dialog.create()
                dialog.show()
            }
        })

        val downloadReceiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                var id = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == download_id){
                    Toast.makeText(applicationContext, "Your download is completed", Toast.LENGTH_LONG).show()
                }
            }
        }
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }// end of onCreate

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

                    this@DownloadFilesActivity.runOnUiThread(java.lang.Runnable {
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
    // using download manager to download files from api/server
    private fun downloadFile(id : Int){

        val uri = Uri.parse("${getString(R.string.host_url)}/download/${id}")

        val storage = getSharedPreferences("app", MODE_PRIVATE)

        val authToken = storage.getString("auth_token", "").toString()

        val request = DownloadManager.Request(uri)
            .addRequestHeader("Authorization", authToken.toString())
            .setTitle(file_name)
            .setDescription("Data leakage is downloading .")
            .setMimeType("text/comma-separated-values")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        download_id = manager.enqueue(request)
    }// downloadFile ends

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
}