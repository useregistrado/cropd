package com.cropd.cropd

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.adapters.CropsAdapter
import com.cropd.cropd.bluetoothmng.BluetoothHandler
import com.cropd.cropd.bluetoothmng.BluetoothManager
import com.cropd.cropd.bluetoothmng.PermissionManager
import com.cropd.cropd.db.DBHelper
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var recyclerCrops: RecyclerView
    lateinit var permissionMng : PermissionManager
    lateinit var bluetoothMng : BluetoothManager
    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        db = DBHelper()
        db.initDatabase()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerCrops = findViewById(R.id.cropsList)
        recyclerCrops.layoutManager = LinearLayoutManager(this)
        val adapter = CropsAdapter(db.selectAllCrops())
        recyclerCrops.adapter = adapter


        permissionMng = PermissionManager(this)
        bluetoothMng = BluetoothManager(this)

        permissionMng.requestPermissions()


        BluetoothManagerSingleton.initialize(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun newCrop(view: View) {

        intent = Intent(this, NewCropActivity::class.java)
        startActivity(intent)
    }

    fun getData(view: View) {
        BluetoothManagerSingleton.sendData("a")
    }

    fun exportData(view: View?) {
        var jsonString = db.getDataString()
        // Creación del archivo
        val fileName = "datos.json"
        val file = File(this.getExternalFilesDir(null), fileName)
        if (jsonString != null) {
            file.writeText(jsonString)
        }

        val fileUri: Uri = FileProvider.getUriForFile(
            this,
            "com.cropd.cropd.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "application/json"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir Archivo"))


        startActivity(Intent.createChooser(shareIntent, "Compartir Archivo"))

    }
}
