package com.dataleakage.dataleakage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataleakage.dataleakage.adaptors.AgentsAdaptor
import com.dataleakage.dataleakage.databinding.ActivityAgentsBinding
import com.dataleakage.dataleakage.models.AgentModel


class AgentsActivity : AppCompatActivity() {
    // binding class attribute
    private lateinit var binding: ActivityAgentsBinding

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // using view binding
        binding = ActivityAgentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting new title on action bar
        supportActionBar?.title = "All Agents"

        // testing part
        val agents = ArrayList<AgentModel>()
        agents.add(AgentModel(id = 1, name = "ahsan", email = "ahsancui@gmail.com", contact = "03425465876", address = "abc"))
        agents.add(AgentModel(id = 2, name = "hamza", email = "hamza@gmail.com", contact = "0345876", address = "abc"))
        agents.add(AgentModel(id = 3, name = "ahsan", email = "ahsancui@gmail.com", contact = "03425465876", address = "abc"))
        agents.add(AgentModel(id = 4, name = "hamza", email = "hamza@gmail.com", contact = "0345876", address = "abc"))

        // recyclerview part
        val adaptor = AgentsAdaptor(this, agents)
        binding.agentsRecyclerview.adapter = adaptor
        binding.agentsRecyclerview.layoutManager = LinearLayoutManager(this)

    }// end of onCreate
}