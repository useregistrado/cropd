package com.cropd.cropd.db

import com.cropd.cropd.model.CropM
import com.cropd.cropd.model.ObservationM
import com.cropd.cropd.model.SamplingM
import com.cropd.cropd.model.WeatherStation
import com.google.gson.Gson
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList
import org.mongodb.kbson.ObjectId
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper {

    val DB_NAME: String = "crops"
    val DB_VERSION: String = "3"
    val DB_FULL_NAME: String = DB_NAME + "_" + DB_VERSION + ".realm"

    fun initDatabase() {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Crop::class,
                Sampling::class,
                Observation::class
            )
        )
            .name(DB_FULL_NAME)
            .build()

        val realm: Realm = Realm.open(config)
        realm.close()
    }

    fun getRealm(): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Crop::class,
                Sampling::class,
                Observation::class
            )
        )
            .name(DB_FULL_NAME)
            .build()

        val realm: Realm = Realm.open(config)
        return realm
    }

    fun selectOneCrop(cropId: ObjectId): Crop? {
        val realm = getRealm()
        val crop: Crop? = realm.query<Crop>("_id == $0", cropId).first().find()
        if (crop != null) {
            return crop
        }
        return null
    }

    suspend fun insertObservation(
        samplingId: ObjectId,
        observation: ObservationM,
        weatherStation: WeatherStation
    ): Boolean {
        val realm = getRealm()
        val date = getDate()

        val newObservation = Observation()
        newObservation.latitude = observation.latitude
        newObservation.longitude = observation.longitude
        newObservation.plantHeight = observation.plantHeight
        newObservation.insufficiencyOf = observation.insufficiencyOf
        newObservation.creationDate = date
        newObservation.leafColor.addAll(observation.leafColor)
        newObservation.symptoms.addAll(observation.symptoms)
        newObservation.preliminaryDiagnosis.addAll(observation.preliminaryDiagnosis)
        newObservation.otherColorLeaf = observation.otherColorLeaf
        newObservation.otherSymptoms = observation.otherSymptoms
        newObservation.otherDiagnosis = observation.otherDiagnosis
        newObservation.sample = observation.sample
        newObservation.observations = observation.observations
        newObservation.modificationDate = date
        newObservation.ws_counter = weatherStation.counter
        newObservation.ws_illumination = weatherStation.illumination
        newObservation.ws_temperature1 = weatherStation.temperature1
        newObservation.ws_humidity1 = weatherStation.humidity1
        newObservation.ws_temperature2 = weatherStation.temperature2
        newObservation.ws_humidity2 = weatherStation.humidity2
        newObservation.ws_soilMoisture = weatherStation.soilMoisture
        newObservation.ws_dateTime = weatherStation.date
        newObservation.ws_time = weatherStation.time
        newObservation.ws_satellites = weatherStation.satellites
        newObservation.ws_hdop = weatherStation.hdop
        newObservation.ws_latitude = weatherStation.latitude
        newObservation.ws_longitude = weatherStation.longitude
        newObservation.ws_dateAge = weatherStation.dateAge
        newObservation.ws_height = weatherStation.height
        newObservation.ws_distance = weatherStation.distance
        newObservation.ws_speed = weatherStation.speed


        return try {
            realm.write {
                val sampling = this.query<Sampling>("_id == $0", samplingId).first().find()
                sampling?.observations?.add(newObservation)
                true // Si llegamos aquí, la transacción fue exitosa
            }
        } catch (e: Exception) {
            e.printStackTrace() // Manejo de la excepción, por ejemplo, imprimiendo el stack trace
            false // Devolvemos false porque la transacción falló
        } finally {
            realm.close() // Asegurarse de cerrar el Realm en un bloque finally para que se cierre sin importar lo que pase
        }
    }

    suspend fun insertSampling(cropId: ObjectId, sampling: SamplingM?): Sampling? {

        val realm = getRealm()
        val sampling = Sampling()
        val date = getDate()
        sampling.creationDate = date
        sampling.lastModification = date
        var resp = false
        try {

            realm.write {
                val crop = this.query<Crop>("_id == $0", cropId).first().find()
                crop?.samplings?.add(sampling)
            }
            resp = true
        } catch (e: Exception) {
            println(e.message)
        }

        realm.close()
        if (resp){
            return  sampling
        }
        return null
    }

    fun insertCrop(crop: CropM): String {
        val realm = getRealm()
        val newCrop = Crop().apply {
            name = crop.name
            address = crop.address
            latitude = crop.latitude
            longitude = crop.longitude
            seedtime = crop.seedtime
            landArea = crop.landArea
            areaUnits = crop.areaUnits
            floorType = crop.floorType
            species = crop.species
            variety = crop.variety
            seed = crop.seed
            previusCrop = crop.previusCrop
            lastModification = crop.lastModification
            creationDate = crop.creationDate
        }
        realm.writeBlocking {
            copyToRealm(newCrop)
        }
        realm.close()
        return newCrop._id.toHexString()
    }

    fun selectAllCrops(): RealmResults<Crop> {
        val realm = getRealm()
        val results = realm.query<Crop>().find()
        return results
    }

    fun selectSamplingsByCrop(cropId: ObjectId): RealmList<Sampling> {
        val realm = getRealm()
        val crop: Crop? = realm.query<Crop>("_id == $0", cropId).first().find()


        if (crop == null) {
            return realmListOf()
        }

        if(crop.samplings == null){
            return  realmListOf()
        }

        return crop.samplings!!
    }

    fun selectOneSampling (idSampling: ObjectId): Sampling? {
        val realm = getRealm()
        val sampling: Sampling? = realm.query<Sampling>("_id == $0", idSampling).first().find()


        val result: Sampling? = if (sampling != null) {
            realm.copyFromRealm(sampling)
        } else {
            null
        }

        realm.close()
        return result
    }

    fun migrations() {
        val colors: ArrayList<String> = ArrayList()
        colors.add("Blanco")
        colors.add("Marrón")
        colors.add("Azul")

        val symptoms: ArrayList<String> = ArrayList()
        symptoms.add("marchitamiento")
        symptoms.add("algo")
        symptoms.add("muerte")

        val preliminar: ArrayList<String> = ArrayList()
        preliminar.add("mordisco de vaca")
        preliminar.add("tizon madrugador")

/*
        val ob1: ObservationM =
            ObservationM(1, 5.225, 3.584, 12.5, colors, symptoms, preliminar, "hierro", "2001-2-1")
        val ob2: ObservationM =
            ObservationM(1, 1.225, 7.584, 12.5, colors, symptoms, preliminar, "cal", "2001-5-2")
        val ob3: ObservationM =
            ObservationM(1, 2.225, 3.4, 12.5, colors, symptoms, preliminar, "hierro", "2001-15-3")
        val ob4: ObservationM =
            ObservationM(1, 3.225, 7.84, 12.5, colors, symptoms, preliminar, "h20", "2001-12-4")
        val ob5: ObservationM = ObservationM(
            1,
            4.225,
            73.584,
            12.5,
            colors,
            symptoms,
            preliminar,
            "hierro",
            "2001-22-6"
        )

        insertObservation(null, ob1)
        insertObservation(null, ob2)
        insertObservation(null, ob3)
        insertObservation(null, ob4)
        insertObservation(null, ob5)*/
    }

    fun migrations2() {
        val sp1 = SamplingM("2024-02-19", "2023-05-4")
        val sp2 = SamplingM("2026-08-12", "2023-05-4")
        val sp3 = SamplingM("2027-01-17", "2023-05-4")
        val sp4 = SamplingM("2023-11-12", "2023-05-4")

        //insertSampling(null, sp1)
        //insertSampling(null, sp2)
        //insertSampling(null, sp3)
        //insertSampling(null, sp4)
    }

    fun migrations3() {
        val crop1 = CropM(
            "Doña luz",
            "debajo del rio",
            5.2,
            7.6,
            "2023-02-19",
            150.5,
            "Ha",
            "Arcilloso",
            "Papa",
            "Pastusa",
            "propia",
            "frijol",
            "2023-06-12",
            "2023-10-23"
        )

        val crop2 = CropM(
            "Pachita",
            "debajo del rio",
            5.2,
            7.6,
            "2023-02-19",
            150.5,
            "Ha",
            "Arcilloso",
            "Papa",
            "Pastusa",
            "propia",
            "frijol",
            "2023-06-12",
            "2023-10-23"
        )

        val crop3 = CropM(
            "Laura",
            "debajo del rio",
            5.2,
            7.6,
            "2023-02-19",
            150.5,
            "Ha",
            "Arcilloso",
            "Papa",
            "Pastusa",
            "propia",
            "frijol",
            "2023-06-12",
            "2023-10-23"
        )

        val crop4 = CropM(
            "El peñol",
            "debajo del rio",
            5.2,
            7.6,
            "2023-02-19",
            150.5,
            "Ha",
            "Arcilloso",
            "Papa",
            "Pastusa",
            "propia",
            "frijol",
            "2023-06-12",
            "2023-10-23"
        )

        insertCrop(crop1)
        insertCrop(crop2)
        insertCrop(crop3)
        insertCrop(crop4)


    }

    fun getDate(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    fun getDataString(): String? {
        val realm = getRealm()

        val crops = realm.query(Crop::class)
            .find()
            .map { realm.copyFromRealm(it) }

        val gson = Gson()
        val jsonString = gson.toJson(crops)

        return jsonString
    }

}