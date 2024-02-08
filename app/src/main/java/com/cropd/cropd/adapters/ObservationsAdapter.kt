package com.cropd.cropd.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.R
import com.cropd.cropd.db.Observation
import io.realm.kotlin.types.RealmList

class ObservationsAdapter(val observations: RealmList<Observation>, val context: Context): RecyclerView.Adapter<ObservationsAdapter.ObservationViewHolder>() {
    class ObservationViewHolder(itemView: View, val adapter: ObservationsAdapter): RecyclerView.ViewHolder(itemView) {
        var viewItem: TextView
        var viewCountSamplingsItem: TextView
        var viewDateObservation: TextView

        init {
            viewItem = itemView.findViewById(R.id.observationItemId)
            viewCountSamplingsItem = itemView.findViewById(R.id.countObservationItemId)
            viewDateObservation = itemView.findViewById(R.id.dateObservationItemId)

            itemView.setOnClickListener(View.OnClickListener {

                val builder = AlertDialog.Builder(adapter.context)
                builder.setTitle("Observación")
                val string = getDataString(adapter.observations.get(adapterPosition))
                builder.setMessage(string)

                builder.setPositiveButton("Aceptar") { dialog, id ->

                }

                val dialogData: AlertDialog = builder.create()
                dialogData.show()
            })
        }

        fun getDataString(obs: Observation): String {

            val diagnostic = adapter.getFirstDiagnosys(obs.preliminaryDiagnosisFungus, obs.preliminaryDiagnosisBacteria, obs.preliminaryDiagnosisVirus, obs.preliminaryDiagnosisPests, obs.preliminaryDiagnosisAbiotic)
            var sintomas = ""

            if (obs.symptoms.isEmpty()){
                sintomas = "Ninguno"
            }

            var leafColorD = ""
            if(obs.leafColor.isEmpty()){
                leafColorD = "No especificado"
            }

            obs.leafColor.forEach{e -> sintomas = sintomas + e + ", "}
            obs.symptoms.forEach{e -> leafColorD = leafColorD + e + ", "}

            val weatherStationDataString = buildString {

                append("Id:\t${obs._id.toHexString()}\n")

                append("Altura planta:\t\t${obs.plantHeight}\n")
                append("Color hojas:\t\t${leafColorD}\n")
                append("Síntomas:\t\t${sintomas}\n")
                append("Diagnóstico:\t\t${diagnostic}\n")

                append("Contador:\t\t\t\t\t${obs.ws_counter}\n")
                append("Iluminación:\t\t\t${obs.ws_illumination}\n")
                append("Temperatura1:\t\t${obs.ws_temperature1}\n")
                append("Humedad1:\t\t\t\t${obs.ws_humidity1}\n")
                append("Temperatura2:\t\t${obs.ws_temperature2}\n")
                append("Humedad2:\t\t\t\t${obs.ws_humidity2}\n")
                append("Humd Suelo:\t\t\t${obs.ws_soilMoisture}\n")
                append("Fecha:\t\t\t\t\t\t\t${obs.ws_dateAge}\n")
                append("Hora:\t\t\t\t\t\t\t\t${obs.ws_time}\n")
                append("Satélites:\t\t\t\t\t${obs.ws_satellites}\n")
                append("HDOP:\t\t\t\t\t\t\t${obs.ws_hdop}\n")
                append("Latitud:\t\t\t\t\t\t${obs.ws_latitude}\n")
                append("Longitud:\t\t\t\t\t${obs.ws_longitude}\n")
                append("Edad del Dato:\t\t${obs.ws_dateAge}\n")
                append("Altura:\t\t\t\t\t\t\t${obs.ws_height}\n")
                append("Distancia:\t\t\t\t\t${obs.ws_distance}\n")
                append("Velocidad:\t\t\t\t\t${obs.ws_speed}\n")
                append("Observaciones:\t\t\t\t\t${obs.observations}\n")
            }

            return weatherStationDataString
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
            val obs = observations[position]
            diagnosys = getFirstDiagnosys(obs.preliminaryDiagnosisFungus, obs.preliminaryDiagnosisBacteria, obs.preliminaryDiagnosisVirus, obs.preliminaryDiagnosisPests, obs.preliminaryDiagnosisAbiotic)
        }
        holder.viewCountSamplingsItem.text = diagnosys
        holder.viewDateObservation.text = observations[position].creationDate
    }

    fun getFirstDiagnosys(arr1: RealmList<String>, arr2: RealmList<String>, arr3: RealmList<String>, arr4: RealmList<String>, arr5: RealmList<String>): String {
        if (!arr1.isEmpty()) return arr1[0]
        if (!arr2.isEmpty()) return arr2[0]
        if (!arr3.isEmpty()) return arr3[0]
        if (!arr4.isEmpty()) return arr4[0]
        if (!arr5.isEmpty()) return arr5[0]

        return "Sin información"
    }
}
