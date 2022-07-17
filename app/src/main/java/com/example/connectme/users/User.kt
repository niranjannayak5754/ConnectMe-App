package com.example.connectme.users

import android.net.Uri

data class User(
    var firstname: String? = null,
    var lastname: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var password: String? = null,
    var username: String? = null,
    var profileImageUrl: String? = null
    )
