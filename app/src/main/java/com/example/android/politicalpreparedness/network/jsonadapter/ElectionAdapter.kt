package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.util.Constants
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class ElectionAdapter {

    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())

    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val districtDelimiter = "district:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        val district = ocdDivisionId.substringAfter(districtDelimiter,"")
            .substringBefore("/")
        return if(state != "") {
            Division(ocdDivisionId, country, state)
        }else{
            Division(ocdDivisionId, country, district)
        }
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }

    @FromJson
    fun dateFromJson(dateStr: String): Date? {
        return dateFormat.parse(dateStr)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return dateFormat.format(date)
    }

}