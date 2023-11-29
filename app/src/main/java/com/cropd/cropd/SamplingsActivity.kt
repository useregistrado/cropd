package com.cropd.cropd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.adapters.SamplingsAdapter
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.db.Sampling
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                id = ""
                println("%%%%%%%%%%%%%111")
            } else {
                id = extras.getString("ID").toString()
                println("%%%%%%%%%%%%%")
            }
        } else {
            id = savedInstanceState.getSerializable("ID").toString()
        }
        titleCrop = findViewById(R.id.titleCropSampling)
        seedDate = findViewById(R.id.seedDate)
        address = findViewById(R.id.address)
        creationDate = findViewById(R.id.creationDate)
        samplings = findViewById(R.id.samplingsCount)
        loadData()
        loadSamplings()
    }


    fun loadData() {
        val db = DBHelper()
        println(id)
        println(id)
        println(id)
        println(id)
        println("#########################")
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

        val adapter = SamplingsAdapter(samplings)
        recyclerSamplings.adapter = adapter
    }

    fun newSampling(view: View) {
        var resp: Sampling? = null
        val db = DBHelper()
        runBlocking {
            launch{
                resp = db.insertSampling(ObjectId(id), null)
            }
        }
        if (resp != null){
            Toast.makeText(this, "Recorrido creado", Toast.LENGTH_LONG).show()
            intent = Intent(this, ObservationsActivity::class.java)
            intent.putExtra("ID", resp!!._id.toHexString())
            startActivity(intent)
        } else {
            Toast.makeText(this, "No se pudo crear el recorrido", Toast.LENGTH_LONG).show()
        }
    }
}