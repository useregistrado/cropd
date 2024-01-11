package com.cropd.cropd.model

class ObservationM(
    var latitude: Double,
    var longitude: Double,
    var plantHeight: Double,
    var leafColor: ArrayList<String>,
    var otherColorLeaf: String,
    var symptoms: ArrayList<String>,
    var otherSymptoms: String,
    var preliminaryDiagnosisFungus: ArrayList<String>,
    var preliminaryDiagnosisBacteria: ArrayList<String>,
    var preliminaryDiagnosisVirus: ArrayList<String>,
    var preliminaryDiagnosisPests: ArrayList<String>,
    var preliminaryDiagnosisAbiotic: ArrayList<String>,
    var captures: ArrayList<String>,
    var otherDiagnosis: String,
    var insufficiencyOf: String,
    var incidence: String,
    var severity: String,
    var insectPopulation: String,
    var sample: String,
    var observations: String
) {
}