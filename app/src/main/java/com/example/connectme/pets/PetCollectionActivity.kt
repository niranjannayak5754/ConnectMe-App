package com.example.connectme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.databinding.ActivityPetCollectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PetCollectionActivity : AppCompatActivity() {
        private lateinit var binding: ActivityPetCollectionBinding
        private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        binding.btnSignOut.setOnClickListener{
            mAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}