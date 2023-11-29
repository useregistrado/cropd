package com.cropd.cropd.model

class ObservationM(
    var latitude: Double,
    var longitude: Double,
    var plantHeight: Double,
    var leafColor: ArrayList<String>,
    var otherColorLeaf: String,
    var symptoms: ArrayList<String>,
    var otherSymptoms: String,
    var preliminaryDiagnosis: ArrayList<String>,
    var otherDiagnosis: String,
    var insufficiencyOf: String,
    var sample: String,
    var observations: String
) {
}