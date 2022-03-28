package com.example.davidschool.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "child_table")
class Child : Serializable {

    @ColumnInfo(name = "Child_Id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "Child_name")
    var childName: String = ""

    @ColumnInfo(name = "Child_phone")
    var childPhone: String = ""

    @ColumnInfo(name = "Child_parent_phone")
    var childParentPhone: String = ""

    @ColumnInfo(name = "Child_school_year")
    var childSchoolYear: String = ""

    @ColumnInfo(name = "Child_address")
    var childAddress: String = ""

    @ColumnInfo(name = "child_abouna")
    var childAbouna: String = ""

    @ColumnInfo(name = "child_photo")
    lateinit var childPhoto: String

    @ColumnInfo(name = "child_birthdate")
    var childBirthdate: String = ""

    @ColumnInfo(name = "child_class_id")
    var childClassId: Int = 0

    @ColumnInfo(name = "child_points")
    var childPoints: Int = 0

    @ColumnInfo(name = "child_gender")
    var childGender: String = ""

    @ColumnInfo(name = "child_shmas_or_not")
    var childShmasOrNot: String = ""

    @ColumnInfo(name = "child_shmas_date")
    var childShmasDate: String = ""

    @ColumnInfo(name = "child_shmas_by")
    var childShmasBy: String = ""

    @ColumnInfo(name = "child_shmas_degree")
    var childShmasDegree: String = ""


}
