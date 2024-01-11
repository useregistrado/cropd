package com.cropd.cropd.bluetoothmng


import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.lang.IllegalStateException

class BluetoothManager(private val context: Context) {

    val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var devices: Set<BluetoothDevice>
    private lateinit var device: BluetoothDevice
    private lateinit var bluetoothHandler: BluetoothHandler
    private lateinit var bluetoothSocket: BluetoothSocket
    private var progressDialog: ProgressDialog
    private var permissionMng: PermissionManager
    private var readingThread: Thread? = null

    @Volatile
    private var isReading: Boolean = false

    init {
        progressDialog = ProgressDialog(context)
        permissionMng = PermissionManager(context)
    }

    fun scanDevices() {
        val check = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        if (check != PackageManager.PERMISSION_GRANTED) {
            permissionMng.requestPermissions()
            return
        }
        devices = adapter.bondedDevices

        devices.forEach { e ->
            print(e.name + " ")
            println(e.address)
        }
    }

    fun connectToDevice(deviceAddress: String) {
        device = adapter.getRemoteDevice(deviceAddress)
        try {
            bluetoothHandler = BluetoothHandler(adapter, deviceAddress)
            bluetoothHandler.start()
            try {
                bluetoothSocket = bluetoothHandler.createSocket(device)
                if (bluetoothSocket.isConnected) {
                    bluetoothSocket.close()
                }
                while(!bluetoothSocket.isConnected) {
                    val check = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    if (check != PackageManager.PERMISSION_GRANTED) {
                        permissionMng.requestPermissions()
                        return
                    }
                    bluetoothSocket.connect()
                }
                if(bluetoothSocket.isConnected){
                    println("conectado")
                    //Toast.makeText(context, "Bluetooth conectado", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                println(e)
                bluetoothSocket.close()
                progressDialog.dismiss()
                //Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            println(e)
            bluetoothSocket.close()
            progressDialog.dismiss()
            //Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
        }

    }

    fun readBluetoothData(onDataReceived: (String) -> Unit) {

        if (readingThread?.isAlive == true) {
            return
        }

        val inputStream = bluetoothSocket.inputStream
        val sensorBuffer = ByteArray(2048)
        var sensorDataBuffer = StringBuilder()
        isReading = true

        readingThread = Thread {
            while (isReading) {
                try {
                    val sensorBytes = inputStream.read(sensorBuffer)
                    if (sensorBytes > 0) {
                        val sensorReceiveData = String(sensorBuffer, 0, sensorBytes)
                        sensorDataBuffer.append(sensorReceiveData)

                        val delimiter = "<"
                        var delimiterIndex: Int

                        // Procesar todos los mensajes completos
                        while (sensorDataBuffer.indexOf(delimiter).also { delimiterIndex = it } != -1) {
                            val completeData = sensorDataBuffer.substring(0, delimiterIndex)
                            onDataReceived(completeData)

                            // Limpiar el buffer hasta el delimitador
                            sensorDataBuffer.delete(0, delimiterIndex + delimiter.length)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        readingThread?.start()
    }




    fun sendData(): Boolean {
        val data = "a"
        println("111")
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected) {
                Thread {
                    try {
                        BluetoothDataStream(bluetoothSocket).sendData(data)
                    } catch (e: IOException) {
                    }
                }.start()
                return true
            } else {
                println("elseeeee")
                return false
            }
        } catch (e: Exception) {
            return false
        }

    }

    fun closeBluetoothConnection() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            println("NO SE PUDO CERRAR EL SOCKET")
        }
    }

    fun stopReading() {
        isReading = false
    }
    fun stopStream() {
        bluetoothSocket?.inputStream?.close()
        bluetoothSocket?.outputStream?.close()
    }

    fun startReading() {
        isReading = true
    }

    fun isConnected(): Boolean {
        try {
            return bluetoothSocket.isConnected
        } catch (e: RuntimeException) {
            return false
        }

    }
}