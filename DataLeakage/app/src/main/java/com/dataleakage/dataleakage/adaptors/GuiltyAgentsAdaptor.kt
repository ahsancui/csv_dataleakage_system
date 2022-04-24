package com.dataleakage.dataleakage.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataleakage.dataleakage.R
import com.dataleakage.dataleakage.models.GuiltyAgentModel

class GuiltyAgentsAdaptor(val context: Context, private val agents:ArrayList<GuiltyAgentModel>):RecyclerView.Adapter<GuiltyAgentsAdaptor.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuiltyAgentsAdaptor.Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guilty_agent_adaptor_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: GuiltyAgentsAdaptor.Viewholder, position: Int) {
        val agent = agents[position]

        holder.name.text = agent.name.toString()
        holder.email.text = agent.email.toString()
        holder.score.text = agent.score.toString()

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return agents.size
    }

    class Viewholder(view:View):RecyclerView.ViewHolder(view){
        val name: TextView
        val email: TextView
        val score: TextView
        init {
            name = view.findViewById(R.id.guilty_agent_name)
            email = view.findViewById(R.id.guilty_agent_email)
            score = view.findViewById(R.id.guilty_agent_score)
        }
    }
}