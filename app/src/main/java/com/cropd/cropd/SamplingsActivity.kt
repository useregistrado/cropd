package com.cropd.cropd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.adapters.SamplingsAdapter
import com.cropd.cropd.db.DBHelper
import org.mongodb.kbson.ObjectId


class SamplingsActivity : AppCompatActivity() {

    var id: String = ""
    lateinit var titleCrop: TextView
    lateinit var seedDate: TextView
    lateinit var address: TextView
    lateinit var creationDate: TextView
    lateinit var samplings: TextView
    lateinit var recyclerSamplings: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samplings)

        id = getCrop()

        titleCrop = findViewById(R.id.titleCropSampling)
        seedDate = findViewById(R.id.seedDate)
        address = findViewById(R.id.address)
        creationDate = findViewById(R.id.creationDate)
        samplings = findViewById(R.id.samplingsCount)
        loadData()
    }


    fun loadData() {
        val db = DBHelper()
        val crop = db.selectOneCrop(ObjectId(id))

        if (crop != null) {
            titleCrop.text = crop.name
            seedDate.text = " " + crop.seedtime
            address.text = " " + crop.address
            creationDate.text = " " + crop.creationDate
            samplings.text = " " + crop.samplings?.size.toString()
        }
    }

    fun loadSamplings() {

        recyclerSamplings = findViewById(R.id.samplingsList)
        recyclerSamplings.layoutManager = LinearLayoutManager(this)

        val db = DBHelper()
        val samplings = db.selectSamplingsByCrop(ObjectId(id))

        val adapter = SamplingsAdapter(samplings, gettSharedPreferences())
        recyclerSamplings.adapter = adapter
    }

    fun newSampling(view: View) {

        intent = Intent(this, NewSamplingActivity::class.java)
        startActivity(intent)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        Toast.makeText(this, "atras", Toast.LENGTH_LONG).show()
    }

    fun gettSharedPreferences(): SharedPreferences {
        val sharedPreferences = getSharedPreferences("bread", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun setSampling(samplingId : String) {
        val sharedPreferences = gettSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putString("sampling", samplingId)
        editor.apply()
    }

    fun getCrop(): String {
        val sharedPreferences = gettSharedPreferences()
        return sharedPreferences.getString("crop", "") ?: ""
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        return true
    }

    override fun onResume() {
        super.onResume()
        loadSamplings() 
    }

}