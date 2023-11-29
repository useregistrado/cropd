package com.cropd.cropd.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.R
import com.cropd.cropd.SamplingsActivity
import com.cropd.cropd.db.Observation
import io.realm.kotlin.types.RealmList

class ObservationsAdapter(val observations: RealmList<Observation>): RecyclerView.Adapter<ObservationsAdapter.ObservationViewHolder>() {
    class ObservationViewHolder(itemView: View, val adapter: ObservationsAdapter): RecyclerView.ViewHolder(itemView) {
        var viewItem: TextView
        var viewCountSamplingsItem: TextView
        var viewDateObservation: TextView

        init {
            viewItem = itemView.findViewById(R.id.observationItemId)
            viewCountSamplingsItem = itemView.findViewById(R.id.countObservationItemId)
            viewDateObservation = itemView.findViewById(R.id.dateObservationItemId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_observation, parent, false)
        return ObservationViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return observations.size
    }

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) {
        holder.viewItem.text = (position + 1).toString()
        var diagnosys = ""
        if(observations[position].observations != null) {
            diagnosys = observations[position].preliminaryDiagnosis?.getOrNull(0) ?: "Sin información"

            println(observations[position].ws_latitude)
        }
        holder.viewCountSamplingsItem.text = diagnosys
        holder.viewDateObservation.text = observations[position].creationDate
    }
}
