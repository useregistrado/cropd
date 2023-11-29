package com.cropd.cropd.db

import com.cropd.cropd.model.CropM
import org.mongodb.kbson.ObjectId

class DbCrop {

    fun selectAll(): ArrayList<CropM> {
        val c1 =
            CropM(
                "Julia",
                "allí",
                52.545,
                53.1551,
                "ayer",
                52.5,
                "Ha",
                "Arcilloso",
                "Papa",
                "pastusa",
                "Certificada",
                "Frijol",
                "ayer",
                "hoy"
            )

        val c2 =
            CropM(
                "Maria",
                "allí",
                52.545,
                53.1551,
                "ayer",
                52.5,
                "Ha",
                "Arcilloso",
                "Papa",
                "pastusa",
                "Certificada",
                "Frijol",
                "ayer",
                "hoy"
            )

        val crops: ArrayList<CropM> = ArrayList()
        crops.add(c1)
        crops.add(c2)
        crops.add(c1)
        crops.add(c2)
        crops.add(c1)
        crops.add(c2)
        crops.add(c1)
        crops.add(c2)
        crops.add(c1)
        crops.add(c2)
        return crops
    }

    fun selectOne(id: ObjectId): CropM {
        val c1 =
            CropM(
                "Julia",
                "allí",
                52.545,
                53.1551,
                "ayer",
                52.5,
                "Ha",
                "Arcilloso",
                "Papa",
                "pastusa",
                "Certificada",
                "Frijol",
                "ayer",
                "hoy"
            )
        return c1
    }
}