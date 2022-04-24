package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataleakage.dataleakage.adaptors.GuiltyAgentsAdaptor
import com.dataleakage.dataleakage.databinding.ActivityFindGuiltyBinding
import com.dataleakage.dataleakage.models.GuiltyAgentModel

class FindGuiltyActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityFindGuiltyBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityFindGuiltyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hiding the actionbar
        supportActionBar?.hide()

        // testing part
        val agents = ArrayList<GuiltyAgentModel>()
        agents.add(GuiltyAgentModel(id=1, name = "abc", email = "abc@gmail.com", score = 100))
        agents.add(GuiltyAgentModel(id=2, name = "56c", email = "56c@gmail.com", score = 50))

        // recyclerview part
        val adaptor = GuiltyAgentsAdaptor(this, agents)
        binding.guiltyAgentRecyclerview.adapter = adaptor
        binding.guiltyAgentRecyclerview.layoutManager = LinearLayoutManager(this)

    }// end of onCreate
}