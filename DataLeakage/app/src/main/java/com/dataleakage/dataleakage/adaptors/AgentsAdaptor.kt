package com.dataleakage.dataleakage.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataleakage.dataleakage.R
import com.dataleakage.dataleakage.models.AgentModel

class AgentsAdaptor(val context: Context, private val agents:ArrayList<AgentModel>):RecyclerView.Adapter<AgentsAdaptor.Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentsAdaptor.Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agent_adaptor_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: AgentsAdaptor.Viewholder, position: Int) {
        val agent = agents[position]

        holder.name.text = agent.name.toString()
        holder.email.text = agent.email.toString()
        holder.phone.text = agent.contact.toString()
        holder.address.text = agent.address.toString()

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return agents.size
    }

    class Viewholder(view:View):RecyclerView.ViewHolder(view){
        val name: TextView
        val email: TextView
        val phone: TextView
        val address: TextView
        init {
            name = view.findViewById(R.id.agent_name)
            email = view.findViewById(R.id.agent_email)
            phone = view.findViewById(R.id.agent_phone)
            address = view.findViewById(R.id.agent_address)
        }
    }
}