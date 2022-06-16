package com.example.connectme.utils.model

import android.os.Parcel
import android.os.Parcelable

data class Pet(
    val petName: String?=null,
    val breedName: String?=null,
    val imageUrl: String?=null,
    val price: String?=null,
    val weight: String?=null,
    val description: String?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(petName)
        parcel.writeString(breedName)
        parcel.writeString(imageUrl)
        parcel.writeString(price)
        parcel.writeString(weight)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }
}