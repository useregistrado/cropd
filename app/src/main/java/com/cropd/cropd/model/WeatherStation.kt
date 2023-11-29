package com.cropd.cropd.model

class WeatherStation(var stringData: String) {

    var counter: String = ""
    var illumination: String = ""
    var temperature1: String = ""
    var humidity1: String = ""
    var temperature2: String = ""
    var humidity2: String = ""
    var soilMoisture: String = ""
    var date: String = ""
    var time: String = ""
    var satellites: String = ""
    var hdop: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var dateAge: String = ""
    var height: String = ""
    var distance: String = ""
    var speed: String = ""

    init {
        val dataParts = stringData.split(",")
        counter = dataParts.getOrElse(0) { "" }
        illumination = dataParts.getOrElse(1) { "" }
        temperature1 = dataParts.getOrElse(2) { "" }
        humidity1 = dataParts.getOrElse(3) { "" }
        temperature2 = dataParts.getOrElse(4) { "" }
        humidity2 = dataParts.getOrElse(5) { "" }
        soilMoisture = dataParts.getOrElse(6) { "" }
        date = dataParts.getOrElse(7) { "" }
        time = dataParts.getOrElse(8) { "" }
        satellites = dataParts.getOrElse(9) { "" }
        hdop = dataParts.getOrElse(10) { "" }
        latitude = dataParts.getOrElse(11) { "" }
        longitude = dataParts.getOrElse(12) { "" }
        dateAge = dataParts.getOrElse(13) { "" }
        height = dataParts.getOrElse(14) { "" }
        distance = dataParts.getOrElse(15) { "" }
        speed = dataParts.getOrElse(16) { "" }
    }



    fun splitString(): String {
        return stringData.replace(",", "\n")
    }

    fun getValues(): String {
        val weatherStationDataString = buildString {
            append("Contador:\t\t\t\t\t${counter}\n")
            append("Iluminación:\t\t\t${illumination}\n")
            append("Temperatura1:\t\t${temperature1}\n")
            append("Humedad1:\t\t\t\t${humidity1}\n")
            append("Temperatura2:\t\t${temperature2}\n")
            append("Humedad2:\t\t\t\t${humidity2}\n")
            append("Humd Suelo:\t\t\t${soilMoisture}\n")
            append("Fecha:\t\t\t\t\t\t\t${date}\n")
            append("Hora:\t\t\t\t\t\t\t\t${time}\n")
            append("Satélites:\t\t\t\t\t${satellites}\n")
            append("HDOP:\t\t\t\t\t\t\t${hdop}\n")
            append("Latitud:\t\t\t\t\t\t${latitude}\n")
            append("Longitud:\t\t\t\t\t${longitude}\n")
            append("Edad del Dato:\t\t${dateAge}\n")
            append("Altura:\t\t\t\t\t\t\t${height}\n")
            append("Distancia:\t\t\t\t\t${distance}\n")
            append("Velocidad:\t\t\t\t\t${speed}\n")
        }

        return weatherStationDataString
    }
}