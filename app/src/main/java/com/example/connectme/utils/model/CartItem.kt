package com.example.connectme.utils.model


data class CartItem(
    var quantity: String = "1",
    var totalPetPrice: String? = null,
    var petId: String? = null,
    var petName: String? = null,
    var breedName: String?=null,
    var imageUrl: String?=null,
    var price: String?=null
)
