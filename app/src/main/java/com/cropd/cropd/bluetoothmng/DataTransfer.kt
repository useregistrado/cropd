package com.cropd.cropd.bluetoothmng

import android.bluetooth.BluetoothSocket
import java.io.OutputStream

class BluetoothDataStream(
    bluetoothSocket: BluetoothSocket
){
    private var bluetoothOutputStream: OutputStream = bluetoothSocket.outputStream
    fun sendData(data: String) {
        bluetoothOutputStream.write(data.toByteArray())
    }
}