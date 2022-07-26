package com.bmi.toshiba.nytapplication.News

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class NewsResponse(

    @SerializedName("results")
    val results : List<Results>

) : Parcelable {
    constructor() : this(mutableListOf())
}
