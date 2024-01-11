package com.cropd.cropd.db

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbObjects {
}

class Crop(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var address: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var seedtime: String = ""
    var landArea: Double = 0.0
    var areaUnits: String = ""
    var analysisSoil: String = ""
    var floorType: String = ""
    var topographyFloor: String = ""
    var distanceForrows: String = ""
    var distancePlants: String = ""
    var species: String = ""
    var variety: String = ""
    var seed: String = ""
    var previusCrop: String = ""
    var lastModification: String = ""
    var creationDate: String = ""

    var electricConductivity: String = ""
    var ph: String = ""
    var calcium: String = ""
    var nitrogen: String = ""
    var phosphorus: String = ""
    var potassium: String = ""
    var sodium: String = ""
    var satAluminum: String = ""

    var samplings: RealmList<com.cropd.cropd.db.Sampling>? = realmListOf()



}


class Sampling(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var cropAge: String = ""
    var observationCrop: String = ""
    var lastModification: String = ""
    var creationDate: String = ""
    var observations: RealmList<Observation>? = realmListOf()
}

class Observation(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var plantHeight: Double = 0.0
    var insufficiencyOf: String = ""
    var leafColor: RealmList<String> = realmListOf()
    var otherColorLeaf: String = ""
    var symptoms: RealmList<String> = realmListOf()
    var otherSymptoms: String = ""

    var preliminaryDiagnosisFungus: RealmList<String> = realmListOf()
    var preliminaryDiagnosisBacteria: RealmList<String> = realmListOf()
    var preliminaryDiagnosisVirus: RealmList<String> = realmListOf()
    var preliminaryDiagnosisPests: RealmList<String> = realmListOf()
    var preliminaryDiagnosisAbiotic: RealmList<String> = realmListOf()
    var captures: RealmList<String> = realmListOf()

    var otherDiagnosis: String = ""

    var incidence: String = ""
    var severity: String = ""
    var insectPopulation: String = ""

    var sample: String = ""
    var observations: String = ""
    var creationDate: String = ""
    var modificationDate: String = ""

    var ws_counter: String = ""
    var ws_illumination: String = ""
    var ws_temperature1: String = ""
    var ws_humidity1: String = ""
    var ws_temperature2: String = ""
    var ws_humidity2: String = ""
    var ws_soilMoisture: String = ""
    var ws_dateTime: String = ""
    var ws_time: String = ""
    var ws_satellites: String = ""
    var ws_hdop: String = ""
    var ws_latitude: String = ""
    var ws_longitude: String = ""
    var ws_dateAge: String = ""
    var ws_height: String = ""
    var ws_distance: String = ""
    var ws_speed: String = ""
}
