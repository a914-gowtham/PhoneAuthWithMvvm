package com.gowtham.firebasephoneauthwithmvvm.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(
    val code: String, val name: String, val noCode: String,
    val money: String) : Parcelable
