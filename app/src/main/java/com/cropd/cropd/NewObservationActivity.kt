package com.cropd.cropd

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.model.ObservationM
import com.cropd.cropd.model.WeatherStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class NewObservationActivity : AppCompatActivity() {

    var id: String = "" //ID Hallazgos

    val colorLeaves: ArrayList<String> = ArrayList()
    val symptoms: ArrayList<String> = ArrayList()
    val diagnosis: ArrayList<String> = ArrayList()

    lateinit var heightPlant: EditText
    lateinit var otherColorLeaves: EditText
    lateinit var otherSymptom: EditText
    lateinit var insufficiency: EditText
    lateinit var otherDiagnosis: EditText
    lateinit var sample: EditText
    lateinit var observations: EditText
    lateinit var dialog: AlertDialog
    lateinit var weatherStation: WeatherStation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_observation)
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                id = ""
            } else {
                id = extras.getString("ID").toString()
            }
        } else {
            id = savedInstanceState.getSerializable("ID").toString()
        }
        loadSettings()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onClickCheck(view: View) {
        val checkBox = view as CheckBox
        val name = resources.getResourceName(checkBox.id)

        if (checkBox.isChecked) {
            checkHandler(checkBox.accessibilityPaneTitle.toString(), name)
        } else {
            unCheckHandler(checkBox.accessibilityPaneTitle.toString(), name)
        }

    }

    fun checkHandler(text: String, name : String) {
        if(Regex("sintoma").containsMatchIn(name)){
            symptoms.add(text)
        }

        if(Regex("color").containsMatchIn(name)){
            colorLeaves.add(text)
        }

        if(Regex("diagnostico").containsMatchIn(name)){
            diagnosis.add(text)
        }
    }

    fun unCheckHandler(text: String, name : String) {
        if(Regex("sintoma").containsMatchIn(name)){
            symptoms.remove(text)
        }

        if(Regex("color").containsMatchIn(name)){
            colorLeaves.remove(text)
        }

        if(Regex("diagnostico").containsMatchIn(name)){
            diagnosis.remove(text)
        }
    }

    fun newObservations(view: View) {

        val height: Double = if (heightPlant.text != null) {
            try {
                heightPlant.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                -1.0
            }
        } else {
            -1.0
        }

        val observation = ObservationM(
            5.32,
            75.682,
            height,
            colorLeaves,
            otherColorLeaves.text.toString(),
            symptoms,
            otherSymptom.text.toString(),
            diagnosis,
            otherDiagnosis.text.toString(),
            insufficiency.text.toString(),
            sample.text.toString(),
            observations.text.toString(),
        )

        val db = DBHelper()

        lifecycleScope.launch {
            val result = db.insertObservation(ObjectId(id), observation, weatherStation)

            // Actualizamos la UI en el hilo principal con withContext
            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(this@NewObservationActivity, "La observación fue insertada con éxito.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@NewObservationActivity, "Hubo un error al insertar la observación.", Toast.LENGTH_LONG).show()
                }

                intent = Intent(this@NewObservationActivity, ObservationsActivity::class.java)
                intent.putExtra("ID", id)
                startActivity(intent)
            }
        }
    }

    fun loadSettings() {
        heightPlant = findViewById(R.id.plant_height)
        otherColorLeaves = findViewById(R.id.color_other)
        otherSymptom = findViewById(R.id.sintoma_otro)
        insufficiency = findViewById(R.id.diagnostico_insuficiencia)
        otherDiagnosis = findViewById(R.id.diagnostico_otro)
        sample = findViewById(R.id.muestra)
        observations = findViewById(R.id.observations)
        weatherStation = WeatherStation("")


        BluetoothManagerSingleton.readBluetoothData {resultado ->
            runOnUiThread {
                dialog.dismiss()
                weatherStation = WeatherStation(resultado)
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Estación meteorológica")
                builder.setMessage(weatherStation.getValues())

                builder.setPositiveButton("Aceptar") { dialog, id ->

                }
                builder.setNegativeButton("Otra vez") { dialog, id ->
                    onClickStation(null)
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    fun onClickStation(view: View?) {
        //Bloqueamos el botón para evitar clicks involuntarios
        if (view != null) {
            view.isEnabled = false
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_progress, null)
        builder.setView(dialogLayout)

        builder.setCancelable(false) // Si quieres que el diálogo no se cancele

        val response = BluetoothManagerSingleton.sendData("a")
        if (!response){
            Toast.makeText(this, "No se pudo establecer la conexión con la estación meteorológica", Toast.LENGTH_LONG).show()

            //Desbloqueamos el botón
            if (view != null) {
                view.postDelayed({ view.isEnabled = true }, 1000)
            }
            return
        }

        dialog = builder.create()
        dialog.show()

        //Desbloqueamos el botón
        if (view != null) {
            view.postDelayed({ view.isEnabled = true }, 1000)
        }
    }
}
