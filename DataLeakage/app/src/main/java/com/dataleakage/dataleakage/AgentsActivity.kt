package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataleakage.dataleakage.adaptors.AgentsAdaptor
import com.dataleakage.dataleakage.databinding.ActivityAgentsBinding
import com.dataleakage.dataleakage.models.AgentModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class AgentsActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityAgentsBinding
    private lateinit var agents:ArrayList<AgentModel>
    private lateinit var adaptor:AgentsAdaptor
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityAgentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "All Agents"

        agents = ArrayList<AgentModel>()
        adaptor = AgentsAdaptor(this, agents)
        // recyclerview part
        binding.agentsRecyclerview.adapter = adaptor
        binding.agentsRecyclerview.layoutManager = LinearLayoutManager(this)

        callAgentApi()

    }// end of onCreate

    private fun callAgentApi(){
        val client = OkHttpClient()
        val storage = getSharedPreferences("app", MODE_PRIVATE)

        val authToken = storage.getString("auth_token", "").toString()

        val url = "${getString(R.string.host_url)}/agent"

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

                    this@AgentsActivity.runOnUiThread(java.lang.Runnable {
                        for( i in 0 until jsonArray.length()){
                            val agent = jsonArray.getJSONObject(i)

                            val id = agent.getInt("id")
                            val name = agent.getString("name")
                            val email = agent.getString("email")
                            val address = agent.getString("address")
                            val contact = agent.getString("contact")

                            agents.add(AgentModel(id = id, name = name, email = email, address = address, contact = contact))
                        }
                        adaptor.notifyDataSetChanged()
                    })
                }
            })
    }
}