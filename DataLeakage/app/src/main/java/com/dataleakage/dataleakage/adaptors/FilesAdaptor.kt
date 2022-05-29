package com.dataleakage.dataleakage.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataleakage.dataleakage.R
import com.dataleakage.dataleakage.models.FileModel

class FilesAdaptor(val context: Context, private val files:ArrayList<FileModel>):RecyclerView.Adapter<FilesAdaptor.Viewholder>() {

    private lateinit var mlistener: FilesAdaptor.onItemClickListener
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: FilesAdaptor.onItemClickListener)
    {
        mlistener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesAdaptor.Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_adaptor_layout, parent, false)
        return Viewholder(view, mlistener)
    }

    override fun onBindViewHolder(holder: FilesAdaptor.Viewholder, position: Int) {
        val file = files[position]

        holder.name.text = file.name.toString()

    }

    override fun getItemCount(): Int {
        return files.size
    }

    class Viewholder(view:View,listener:FilesAdaptor.onItemClickListener):RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.file_name)

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}