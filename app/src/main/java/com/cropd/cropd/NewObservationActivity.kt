package com.cropd.cropd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import androidx.lifecycle.lifecycleScope
import com.cropd.cropd.bluetoothmng.BluetoothManager
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.model.ObservationM
import com.cropd.cropd.model.WeatherStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.mongodb.kbson.ObjectId
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale

class NewObservationActivity : AppCompatActivity(), LocationListener {

    var id: String = ""

    val colorLeaves: ArrayList<String> = ArrayList()
    val symptoms: ArrayList<String> = ArrayList()

    val diagnosisFungus: ArrayList<String> = ArrayList()
    val diagnosisBacteria: ArrayList<String> = ArrayList()
    val diagnosisVirus: ArrayList<String> = ArrayList()
    val diagnosisPests: ArrayList<String> = ArrayList()
    val diagnosisAbioticFactors: ArrayList<String> = ArrayList()
    val captures: ArrayList<String> = ArrayList()

    lateinit var heightPlant: EditText
    lateinit var otherColorLeaves: EditText
    lateinit var otherSymptom: EditText
    lateinit var insufficiency: EditText
    lateinit var otherDiagnosis: EditText

    lateinit var incidence: EditText
    lateinit var severity: EditText
    lateinit var insectPopulation: EditText

    lateinit var sample: EditText
    lateinit var observations: EditText
    lateinit var dialog: AlertDialog
    lateinit var weatherStation: WeatherStation
    lateinit var blue: BluetoothManager
    lateinit var cropLatitude: TextView
    lateinit var cropLongitude: TextView
    private val activityScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var locationManager: LocationManager

    private lateinit var confirmView: TextView

    private var stationData = false
    private lateinit var currentPhotoPath: String

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_observation)

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
        println(name)
        if(Regex("sintoma").containsMatchIn(name)){
            symptoms.add(text)
        }

        if(Regex("color").containsMatchIn(name)){
            colorLeaves.add(text)
        }

        if(Regex("fungus").containsMatchIn(name)){
            diagnosisFungus.add(text)
        }

        if(Regex("bacteria").containsMatchIn(name)){
            diagnosisBacteria.add(text)
        }

        if(Regex("virus").containsMatchIn(name)){
            diagnosisVirus.add(text)
        }

        if(Regex("pests").containsMatchIn(name)){
            diagnosisPests.add(text)
        }

        if(Regex("abiotic").containsMatchIn(name)){
            diagnosisAbioticFactors.add(text)
        }
        verifyData()
    }

    fun unCheckHandler(text: String, name : String) {
        if(Regex("sintoma").containsMatchIn(name)){
            symptoms.remove(text)
        }

        if(Regex("color").containsMatchIn(name)){
            colorLeaves.remove(text)
        }

        if(Regex("fungus").containsMatchIn(name)){
            diagnosisFungus.remove(text)
        }

        if(Regex("bacteria").containsMatchIn(name)){
            diagnosisBacteria.remove(text)
        }

        if(Regex("virus").containsMatchIn(name)){
            diagnosisVirus.remove(text)
        }

        if(Regex("pests").containsMatchIn(name)){
            diagnosisPests.remove(text)
        }

        if(Regex("abiotic").containsMatchIn(name)){
            diagnosisAbioticFactors.remove(text)
        }

        verifyData()
    }

    fun newObservations(view: View) {
        //BluetoothManagerSingleton.stopReading()
        blue.stopReading()
        val height: Double = if (heightPlant.text != null) {
            try {
                heightPlant.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                -1.0
            }
        } else {
            -1.0
        }

        val latitude: Double = if (cropLatitude.text != null) {
            try {
                cropLatitude.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        } else {
            0.0
        }

        val longitude: Double = if (cropLongitude.text != null) {
            try {
                cropLongitude.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        } else {
            0.0
        }

        val observation = ObservationM(
            latitude,
            longitude,
            height,
            colorLeaves,
            otherColorLeaves.text.toString(),
            symptoms,
            otherSymptom.text.toString(),
            diagnosisFungus,
            diagnosisBacteria,
            diagnosisVirus,
            diagnosisPests,
            diagnosisAbioticFactors,
            captures,
            otherDiagnosis.text.toString(),
            insufficiency.text.toString(),

            incidence.text.toString(),
            severity.text.toString(),
            insectPopulation.text.toString(),

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
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
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

        incidence = findViewById(R.id.incidence)
        severity = findViewById(R.id.severity)
        insectPopulation = findViewById(R.id.insect_population)

        sample = findViewById(R.id.muestra)
        observations = findViewById(R.id.observations)
        weatherStation = WeatherStation("")
        blue = BluetoothManager(this)
        cropLatitude = findViewById(R.id.observation_latitude)
        cropLongitude = findViewById(R.id.observation_longitude)
        confirmView = findViewById(R.id.confirm)
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

        //val response = BluetoothManagerSingleton.sendData("a")
        val response = blue.sendData()
        if (!response){
            Toast.makeText(this, "No se pudo establecer la conexión con la estación meteorológica", Toast.LENGTH_LONG).show()

            //Desbloqueamos el botón
            if (view != null) {
                view.postDelayed({ view.isEnabled = true }, 1000)
            }
            return
        }
        //BluetoothManagerSingleton.startReading()
        blue.startReading()

        dialog = builder.create()
        dialog.show()

        //Desbloqueamos el botón
        if (view != null) {
            view.postDelayed({ view.isEnabled = true }, 1000)
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            locationManager.removeUpdates(this)
            blue.stopStream()
        } catch (e: Exception) { }

    }

    fun loadBluetooth(view: View?) {
        val isConnected = blue.isConnected()

        if (!isConnected) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    blue.connectToDevice("00:21:13:01:DE:5F")
                    readBluetoothData()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewObservationActivity, "Conectado", Toast.LENGTH_LONG).show()
                    }
                } catch (e: RuntimeException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewObservationActivity, "Bluetooth NO conectado, por favor enciende el bluetooth", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Si ya está conectado, proceder con la lectura de datos de Bluetooth
            readBluetoothData()
        }
    }

    fun readBluetoothData() {
        blue.readBluetoothData {resultado ->

            runOnUiThread {
                println("new obs" + resultado)
                if(this::dialog.isInitialized) {
                    dialog.dismiss()
                }
                weatherStation = WeatherStation(resultado)
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Estación meteorológica")
                builder.setMessage(weatherStation.getValues())

                builder.setPositiveButton("Aceptar") { dialog, id ->
                    stationData = true
                    verifyData()
                }
                builder.setNegativeButton("Otra vez") { dialog, id ->
                    onClickStation(null)
                }
                val dialogData: AlertDialog = builder.create()
                dialogData.show()
            }
        }
    }

    fun gettSharedPreferences(): SharedPreferences {
        val sharedPreferences = getSharedPreferences("bread", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun getSampling(): String {
        val sharedPreferences = gettSharedPreferences()
        return sharedPreferences.getString("sampling", "") ?: ""
    }

    fun initGps() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        // Solicitar actualizaciones de ubicación
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            2000,  // intervalo de tiempo mínimo en milisegundos
            1f,    // distancia mínima en metros
            this // tu LocationListener
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this@NewObservationActivity, "Cargando...", Toast.LENGTH_SHORT).show()

        initGps()
        try {
            id = getSampling()
        } catch (e: RuntimeException) {
            Toast.makeText(this, "Error con el ID", Toast.LENGTH_SHORT).show()
        }

        loadBluetooth(null)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("NewObservationActivity", "Nueva ubicación: Latitud ${location.latitude}, Longitud ${location.longitude}, A: ${location.accuracy}")
        cropLatitude.text = location.latitude.toString()
        cropLongitude.text = location.longitude.toString()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

     fun verifyData() {
         println("verify")
         var faltantes = ""

         if (!stationData){
             faltantes = faltantes + "Estación, "
         }

         if(symptoms.isEmpty()){
             faltantes = faltantes + "Síntomas, "
         }


         if (!(!diagnosisFungus.isEmpty() || !diagnosisBacteria.isEmpty() || !diagnosisVirus.isEmpty() || !diagnosisPests.isEmpty() || !diagnosisAbioticFactors.isEmpty())){
             faltantes = faltantes + "Diagnóstico, "
         }

         if (captures.isEmpty()){
             faltantes = faltantes + "Fotos"
         }

         if (faltantes.endsWith(", ")) {
             faltantes = faltantes.removeSuffix(", ")
         }

         if(faltantes.equals("")){
             faltantes = "Ningún dato"
         }

         confirmView.text = " " + faltantes
     }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, ObservationsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        return true
    }

    fun dispatchTakePictureIntent(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.cropd.cropd",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println(currentPhotoPath)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageFile = File(currentPhotoPath)
            val timeStamp: String = SimpleDateFormat("yyyy-MM-dd_HH-m-mss", Locale.getDefault()).format(Date())
            val name = id + "_" + timeStamp + ".jpeg"
            val response = saveFileToMicroSD(this, imageFile, name)
            if (response){
                captures.add(name)
                verifyData()
            }
        }
    }
    fun saveFileToMicroSD(context: Context, sourceFile: File, fileName: String): Boolean {
        val microSD = getMicroSDCardPath(context) ?: return false
        val destinationFile = File(microSD, fileName)
        if (!destinationFile.parentFile.exists()) {
            destinationFile.parentFile.mkdirs()
        }

        return try {
            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getMicroSDCardPath(context: Context): File? {
        val externalFilesDirs = context.getExternalFilesDirs(null)
        externalFilesDirs.forEach { file ->
            if (file != null && Environment.isExternalStorageRemovable(file)) {

                if (file.canWrite() && Environment.getExternalStorageDirectory() != file) {
                    return file
                }
            }
        }
        return null
    }
}
