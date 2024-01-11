package com.cropd.cropd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.db.Sampling
import com.cropd.cropd.model.SamplingM
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mongodb.kbson.ObjectId

class NewSamplingActivity : AppCompatActivity() {
    var id = ""

    lateinit var cropAge: Spinner
    lateinit var observationsView: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sampling)
        id = getCrop()
        loadSetting()
    }

    fun createSampling(view: View) {
        var resp: Sampling? = null
        val samplingdto = SamplingM(cropAge.selectedItem.toString(), observationsView.text.toString())
        val db = DBHelper()
        runBlocking {
            launch{
                resp = db.insertSampling(ObjectId(id), samplingdto)
            }
        }
        if (resp != null){
            Toast.makeText(this, "Recorrido creado", Toast.LENGTH_LONG).show()
            intent = Intent(this, ObservationsActivity::class.java)
            val idSamplingNew = resp!!._id.toHexString()
            intent.putExtra("ID", idSamplingNew)

            setSampling(idSamplingNew)
            startActivity(intent)
        } else {
            Toast.makeText(this, "No se pudo crear el recorrido", Toast.LENGTH_LONG).show()
        }
    }

    fun setSampling(samplingId : String) {
        val sharedPreferences = gettSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putString("sampling", samplingId)
        editor.apply()
    }

    fun gettSharedPreferences(): SharedPreferences {
        val sharedPreferences = getSharedPreferences("bread", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun getCrop(): String {
        val sharedPreferences = gettSharedPreferences()
        return sharedPreferences.getString("crop", "") ?: ""
    }

    fun loadSetting() {
        cropAge = findViewById(R.id.crop_age)
        observationsView = findViewById(R.id.observations_sampling)
    }
}