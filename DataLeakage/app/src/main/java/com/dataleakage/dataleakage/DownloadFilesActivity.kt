package com.dataleakage.dataleakage

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.dataleakage.dataleakage.adaptors.FilesAdaptor
import com.dataleakage.dataleakage.databinding.ActivityDownloadFilesBinding
import com.dataleakage.dataleakage.models.FileModel
import okhttp3.*
import org.json.JSONArray
import java.io.*
import java.lang.Exception

class DownloadFilesActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityDownloadFilesBinding

    private lateinit var files:ArrayList<FileModel>

    private lateinit var adaptor:FilesAdaptor

    private  var file_name = "Data leakage download"

    private lateinit var progressDialog: ProgressDialog

    private val PERMISSION_REQUEST_CODE = 50

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityDownloadFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Download Files"

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Download")

        setupPermissions()

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
                            download(id)
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                dialog.create()
                dialog.show()
            }
        })
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

    // using okhttp to download csv file
    private  fun download(id:Int){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        val authToken = storage.getString("auth_token", "").toString()

        val url = "${getString(R.string.host_url)}/download/${id}"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", authToken.toString())
            .get()
            .build()

        progressDialog.setMessage("Downloading file...")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val stream = response?.body?.bytes()

                try {
                    val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file_name+".csv")
                    val writer = FileWriter(path)
                    writer.write(stream!!.decodeToString())
                    writer.flush()
                    writer.close()

                    this@DownloadFilesActivity.runOnUiThread(java.lang.Runnable {
                        progressDialog.hide()
                        Toast.makeText(this@DownloadFilesActivity, "Your download is completed", Toast.LENGTH_LONG).show()
                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
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

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(ContentValues.TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(ContentValues.TAG, "Permission has been denied by user")
                } else {
                    Log.i(ContentValues.TAG, "Permission has been granted by user")
                }
            }
        }
    }
}