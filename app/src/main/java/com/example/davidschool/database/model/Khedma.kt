package com.example.davidschool.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "khedma_table")
class Khedma {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "khedma_id")
    var meetingId: Int = 0


    @ColumnInfo(name = "khedma_name")
    var meetingName: String = ""

    @ColumnInfo(name = "khedma_school_year")
    var meetingSchoolYear: String = ""

    @ColumnInfo(name = "khedma_photo")
    var meetingPhoto: String = ""

    @ColumnInfo(name = "Birthday_Message")
    var birthdayMessage: String = ""

    @ColumnInfo(name = "Shmas_Message")
    var shmasMessage: String = ""

    @ColumnInfo(name = "Eftekad_Message")
    var eftekadMessage: String = ""
}
