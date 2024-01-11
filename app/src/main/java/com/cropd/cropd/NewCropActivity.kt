package com.cropd.cropd

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cropd.cropd.db.DBHelper
import com.cropd.cropd.model.CropM
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewCropActivity : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var address: EditText
    lateinit var date: TextView
    lateinit var area: EditText
    lateinit var areaUnits: Spinner
    lateinit var analysisSoil: Spinner
    lateinit var floorType: Spinner
    lateinit var topographyFloor: Spinner
    lateinit var distanceForrows: EditText
    lateinit var distancePlants: EditText
    lateinit var seed: EditText
    lateinit var specie: EditText
    lateinit var variety: EditText
    lateinit var lastCrop: EditText
    lateinit var electricConductivity: EditText
    lateinit var ph: EditText
    lateinit var calcium: EditText
    lateinit var nitrogen: EditText
    lateinit var phosphorus: EditText
    lateinit var potassium: EditText
    lateinit var sodium: EditText
    lateinit var satAluminum: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_crop)

        val textViewSelectedDate: TextView = findViewById(R.id.textViewSelectedDate)
        val buttonDatePicker: Button = findViewById(R.id.buttonDatePicker)

        buttonDatePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    textViewSelectedDate.text = selectedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        initSettings()
    }

    fun saveCrop(view: View) {

        val currentDate = getDate()

        val newCrop = CropM(
            name.text.toString(),
            address.text.toString(),
            5.8526,
            75.2585,
            date.text.toString(),
            area.text.toString().toDouble(),
            areaUnits.selectedItem.toString(),
            analysisSoil.selectedItem.toString(),
            floorType.selectedItem.toString(),
            topographyFloor.selectedItem.toString(),
            distanceForrows.text.toString(),
            distancePlants.text.toString(),
            specie.text.toString(),
            variety.text.toString(),
            seed.text.toString(),
            lastCrop.text.toString(),
            electricConductivity.text.toString(),
            ph.text.toString(),
            calcium.text.toString(),
            nitrogen.text.toString(),
            phosphorus.text.toString(),
            potassium.text.toString(),
            sodium.text.toString(),
            satAluminum.text.toString(),
            currentDate,
            currentDate
        )

        try {
            val db = DBHelper()
            val idCrop = db.insertCrop(newCrop)
            Toast.makeText(this, "Guardado", Toast.LENGTH_LONG).show()
            intent = Intent(this, SamplingsActivity::class.java)
            setCrop(idCrop)
            startActivity(intent)
        } catch (e : Exception) {
            Toast.makeText(this, "Error al crear el cultivo", Toast.LENGTH_LONG).show()
        }


    }

    fun initSettings () {
        name = findViewById(R.id.name_new_crop)
        address = findViewById(R.id.address_new_crop)
        date = findViewById(R.id.textViewSelectedDate)
        area = findViewById(R.id.area_new_crop)
        areaUnits = findViewById(R.id.area_units_new_crop)
        analysisSoil = findViewById(R.id.yes_no_ns_nr)
        floorType = findViewById(R.id.floor_type_new_crop)
        topographyFloor = findViewById(R.id.floor_topography_new_crop)
        distanceForrows = findViewById(R.id.distance_new_crop)
        distancePlants = findViewById(R.id.distance_plants_new_crop)
        specie = findViewById(R.id.specie_new_crop)
        variety = findViewById(R.id.variety_new_crop)
        lastCrop = findViewById(R.id.last_crop_new_crop)
        seed = findViewById(R.id.seed_new_crop)
        electricConductivity = findViewById(R.id.conductivity_new_crop)
        ph = findViewById(R.id.ph_new_crop)
        calcium = findViewById(R.id.calcium_new_crop)
        nitrogen = findViewById(R.id.nitrogen_new_crop)
        phosphorus = findViewById(R.id.phosphorous_new_crop)
        potassium = findViewById(R.id.potassium_new_crop)
        sodium = findViewById(R.id.sodium_new_crop)
        satAluminum = findViewById(R.id.sat_aluminum_new_crop)
    }

    fun getDate(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    fun gettSharedPreferences(): SharedPreferences {
        val sharedPreferences = getSharedPreferences("bread", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun setCrop(cropId : String) {
        val sharedPreferences = gettSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putString("crop", cropId)
        editor.apply()
    }
}