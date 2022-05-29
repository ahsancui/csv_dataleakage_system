package com.dataleakage.dataleakage

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataleakage.dataleakage.adaptors.FilesAdaptor
import com.dataleakage.dataleakage.adaptors.GuiltyAgentsAdaptor
import com.dataleakage.dataleakage.databinding.ActivityFindGuiltyBinding
import com.dataleakage.dataleakage.models.FileModel
import com.dataleakage.dataleakage.models.GuiltyAgentModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FindGuiltyActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityFindGuiltyBinding
    private val agents = ArrayList<GuiltyAgentModel>()

    private var originalFileId:Int = -1

    private var guiltyFileUri: Uri?= null

    private val PERMISSION_REQUEST_CODE = 50

    private lateinit var adaptor: GuiltyAgentsAdaptor

    private lateinit var progressDialog:ProgressDialog

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityFindGuiltyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hiding the actionbar
        supportActionBar?.hide()

        // getting runtime permissions
        setupPermissions()

        // recyclerview part
        adaptor = GuiltyAgentsAdaptor(this, agents)
        binding.guiltyAgentRecyclerview.adapter = adaptor
        binding.guiltyAgentRecyclerview.layoutManager = LinearLayoutManager(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Data Leakage")

        binding.chooseGuiltyFileBtn.setOnClickListener {
            chooseGuiltyFile()
        }
        binding.selectOrigionalFileBtn.setOnClickListener {
            selectOriginalFile()
        }
        binding.findGuiltyPersonBtn.setOnClickListener {
            if (originalFileId <= -1){
              binding.guiltyError.text = "Please select original file!"
            }
            else if(guiltyFileUri == null){
                binding.guiltyError.text = "Please choose guilty file!"
            }
            else{
                agents.clear()
                callGuiltyApi()
            }
        }
    }// end of onCreate

    private fun selectOriginalFile(){
        val intent = Intent(this, SelectFileActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            originalFileId = data!!.getIntExtra("id", -1)
            binding.selectOrigionalFileBtn.text = data!!.getStringExtra("name")
        }
        else if (requestCode == 111 && resultCode == RESULT_OK){
            guiltyFileUri = data?.data
        }
    }

    private fun chooseGuiltyFile(){
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select a csv file"), 111)
    }

    private fun callGuiltyApi(){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)


        val authToken = storage.getString("auth_token", "").toString()

        val url = "${getString(R.string.host_url)}/guilty/${originalFileId}"

        val name = getFileName(guiltyFileUri!!)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator.toString() +
                    name)

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            //.addFormDataPart("file", "file")
            .addFormDataPart("clone_file", name, RequestBody.create("text/comma-separated-values".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", authToken)
            .post(body)
            .build()

        progressDialog.setMessage("Finding guilty...")
        progressDialog.show()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.d("body", body.toString())
                    this@FindGuiltyActivity.runOnUiThread(Runnable {
                        if(response.code == 200){
                            val jsonArray = JSONArray(body)
                            for( i in 0 until jsonArray.length()){
                                val agent = jsonArray.getJSONObject(i)

                                val id = agent.getInt("id")
                                val name = agent.getString("name")
                                val email = agent.getString("email")
                                val score = agent.getInt("percent")
                                agents.add(GuiltyAgentModel(id = id, name = name, email = email, score = score ))
                            }
                            adaptor.notifyDataSetChanged()
                        }
                        else if(response.code == 404){
                            val jsonObject = JSONObject(body)
                            val error = jsonObject.getString("detail")
                            binding.guiltyError.text = error
                        }
                        progressDialog.hide()
                    })
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

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(ContentValues.TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
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