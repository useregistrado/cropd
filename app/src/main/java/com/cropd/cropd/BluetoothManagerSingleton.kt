package com.cropd.cropd

import android.content.Context
import android.widget.Toast
import com.cropd.cropd.bluetoothmng.BluetoothManager
import java.io.IOException
import java.lang.RuntimeException

object BluetoothManagerSingleton {
    lateinit var bluetoothManager: BluetoothManager

    fun initialize(context: Context) {
        try {
            bluetoothManager = BluetoothManager(context.applicationContext)
            bluetoothManager.connectToDevice("00:21:13:01:DE:5F")
        } catch (e: RuntimeException) {
            Toast.makeText(context, "Bluetooth NO conectado, por favor enciente el bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    fun readBluetoothData(callback: (String) -> Unit) {
        bluetoothManager.readBluetoothData(callback)
    }

    fun sendData(string: String): Boolean {
        return bluetoothManager.sendData()
    }
}
