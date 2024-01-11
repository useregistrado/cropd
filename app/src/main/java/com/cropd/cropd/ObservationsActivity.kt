package com.cropd.cropd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.adapters.ObservationsAdapter
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.db.Observation
import io.realm.kotlin.types.RealmList
import org.mongodb.kbson.ObjectId
import java.io.IOException

class ObservationsActivity : AppCompatActivity() {
    var id: String = ""
    lateinit var idTextView: TextView
    lateinit var creationDate: TextView
    lateinit var lastModificationDate: TextView
    lateinit var countObservations: TextView
    lateinit var recyclerObservations: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observations)
         id = getSampling()

        loadSettings()
        loadData()
    }

    fun loadData() {
        val db = DBHelper()

        val sampling = db.selectOneSampling(ObjectId(id))
        idTextView.text = id

        if (sampling != null) {
            creationDate.text = " " + sampling.creationDate
            lastModificationDate.text = " " + sampling.lastModification
            countObservations.text = " " + sampling.observations?.size.toString()
            loadObservations(sampling.observations)
        }
    }

    fun loadSettings() {
        idTextView = findViewById(R.id.idCropObservations)
        creationDate = findViewById(R.id.observationsCreationDate)
        lastModificationDate = findViewById(R.id.observationLastDate)
        countObservations = findViewById(R.id.observationsCount)
        recyclerObservations = findViewById(R.id.observationsList)
    }

    fun newObservation(view: View) {
        val intent = Intent(this, NewObservationActivity::class.java)
        intent.putExtra("ID", id)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun loadObservations(observations: RealmList<Observation>?) {
        if(observations == null) {
            return
        }
        recyclerObservations.layoutManager = LinearLayoutManager(this)
        val adapter = ObservationsAdapter(observations, this)
        recyclerObservations.adapter = adapter
    }

    fun gettSharedPreferences(): SharedPreferences {
        val sharedPreferences = getSharedPreferences("bread", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun getSampling(): String {
        val sharedPreferences = gettSharedPreferences()
        return sharedPreferences.getString("sampling", "") ?: ""
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, SamplingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        return true
    }
}