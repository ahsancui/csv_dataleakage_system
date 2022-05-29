package com.dataleakage.dataleakage

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import com.dataleakage.dataleakage.databinding.ActivityUploadFileBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import android.widget.Toast
import java.io.*
import java.lang.StringBuilder
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import android.provider.OpenableColumns





class UploadFileActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityUploadFileBinding

    private lateinit var progressDialog: ProgressDialog

    private var uri:Uri? = null

    private val PERMISSION_REQUEST_CODE = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityUploadFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "Upload File"

        binding.uploadFileBtn.visibility = View.VISIBLE

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Data Leakage")

        setupPermissions()

        binding.uploadChooseBtn.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Select a csv file"), 111)
        }

        binding.uploadFileBtn.setOnClickListener {
            uploadFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {

            uri = data?.data
            if(uri != null){
                //contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //binding.uploadChooseBtn.text = file?.name.toString()
                binding.uploadFileBtn.visibility = View.VISIBLE
            }
        }
    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }

    }

    private fun uploadFile(){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)


        val authToken = storage.getString("auth_token", "").toString()

        val url = "${getString(R.string.host_url)}/upload"

        val name = getFileName(uri!!)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator.toString() +
                    name)

        val body =MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            //.addFormDataPart("file", "file")
            .addFormDataPart("file", name, RequestBody.create("text/comma-separated-values".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", authToken)
            .post(body)
            .build()

        progressDialog.setMessage("Uploading file...")
        progressDialog.show()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    // converting response body in json object
                    val jsonObject = JSONObject(response.body?.string())

                    // if the response is not Ok then displaying the error
                    if(response.code == 200 || response.code == 400){
                        val error = jsonObject.getString("detail")
                        this@UploadFileActivity.runOnUiThread(Runnable {
                            binding.fileUploadError.text = error.toString()
                            binding.uploadChooseBtn.text = "Choose csv file"
                            progressDialog.hide()
                            Toast.makeText(applicationContext, "File upload completed", Toast.LENGTH_LONG).show()
                        })
                    }
                }
            })
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}