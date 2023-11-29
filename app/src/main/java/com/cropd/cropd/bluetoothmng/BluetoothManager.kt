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

class BluetoothManager(private val context: Context) {

    val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var devices: Set<BluetoothDevice>
    private lateinit var device: BluetoothDevice
    private lateinit var bluetoothHandler: BluetoothHandler
    private lateinit var bluetoothSocket: BluetoothSocket
    private var progressDialog: ProgressDialog
    private var permissionMng: PermissionManager

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
                    Toast.makeText(context, "Bluetooth conectado", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                bluetoothSocket.close()
                progressDialog.dismiss()
                Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            bluetoothSocket.close()
            progressDialog.dismiss()
            Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
        }

    }

    fun readBluetoothData(onDataReceived: (String) -> Unit) {
        val inputStream = bluetoothSocket.inputStream
        val sensorBuffer = ByteArray(1024)
        var sensorDataBuffer = StringBuilder()

        Thread {
            while (true) {
                try {
                    val sensorBytes = inputStream.read(sensorBuffer)
                    if (sensorBytes > 0) {
                        val sensorReceiveData = String(sensorBuffer, 0, sensorBytes)
                        sensorDataBuffer.append(sensorReceiveData)

                        // Verificar si se ha recibido un fragmento completo (por ejemplo, el delimitador \n)
                        val delimiter = "\n"
                        val data = sensorDataBuffer.toString()
                        if (data.contains(delimiter)) {
                            val parts = data.split(delimiter)
                            val completeData = parts[0]  // Tomar el primer fragmento completo

                            // Enviar los datos al callback
                            onDataReceived(completeData)

                            // Eliminar el fragmento completo del buffer
                            sensorDataBuffer = StringBuilder(data.substring(completeData.length + delimiter.length))
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }.start()
    }

    fun sendData(): Boolean {
        val data = "a"

        if (bluetoothSocket != null && bluetoothSocket.isConnected) {
            Thread {
                try {
                    BluetoothDataStream(bluetoothSocket).sendData(data)
                } catch (e: IOException) {
                    // Manejar el error, por ejemplo, reconectar o informar al usuario
                }
            }.start()
            return true
        } else {
            return false
        }

    }

}