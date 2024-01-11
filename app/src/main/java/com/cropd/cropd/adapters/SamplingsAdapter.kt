package com.cropd.cropd.adapters

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.ObservationsActivity
import com.cropd.cropd.R
import com.cropd.cropd.db.Sampling
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList

class SamplingsAdapter(val samplings: RealmList<Sampling>, val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<SamplingsAdapter.SamplingViewHolder>(){
    class SamplingViewHolder(itemView: View, val adapter: SamplingsAdapter) : RecyclerView.ViewHolder(itemView)  {
        var viewItem: TextView
        var viewCountSamplingsItem: TextView
        var viewDateSampling: TextView

        init {
            viewItem = itemView.findViewById(R.id.samplingItemId)
            viewCountSamplingsItem = itemView.findViewById(R.id.countSamplingsItemId)
            viewDateSampling = itemView.findViewById(R.id.dateSamplingsItemId)

            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(itemView.context, ObservationsActivity::class.java)
                val id = adapter.samplings.get(adapterPosition)._id.toHexString()
                intent.putExtra("ID", id)
                adapter.setSampling(id)
                itemView.context.startActivity(intent)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SamplingsAdapter.SamplingViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_sampling, parent, false)
        return SamplingViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return samplings.size
    }

    override fun onBindViewHolder(holder: SamplingsAdapter.SamplingViewHolder, position: Int) {
        holder.viewItem.text = (position + 1).toString()
        var count: Int = 0
        if(samplings[position].observations != null) {
            count = samplings[position].observations?.size ?: 0
        }
        holder.viewCountSamplingsItem.text = "# Observaciones: " + count.toString()
        holder.viewDateSampling.text = samplings[position].creationDate
    }

    fun setSampling(samplingId : String) {
        val editor = sharedPreferences.edit()
        editor.putString("sampling", samplingId)
        editor.apply()
    }
}