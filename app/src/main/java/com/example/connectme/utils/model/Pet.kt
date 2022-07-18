package com.example.connectme.utils.model

import android.os.Parcel
import android.os.Parcelable

data class Pet(
    var petId: String? =null,
    var petName: String?=null,
    var breedName: String?=null,
    var imageUrl: String?=null,
    var price: String?=null,
    var weight: String?=null,
    var description: String?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(petId)
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