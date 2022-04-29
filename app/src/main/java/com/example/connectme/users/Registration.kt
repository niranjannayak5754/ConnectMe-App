package com.example.connectme.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.connectme.MainActivity
import com.example.connectme.R
import com.example.connectme.databinding.ActivityRegistrationBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        binding.btnRegister.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE

            val firstname = binding.etFirstName.text.toString()
            val lastname = binding.etLastName.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val phone = binding.etPhoneNum.text.toString()
            val address = binding.etAddress.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Users")
            val user = User(firstname, lastname, phone, address, password, username)
            database.child(username).setValue(user).addOnSuccessListener {

                binding.etFirstName.text.clear()
                binding.etLastName.text.clear()
                binding.etUsername.text.clear()
                binding.etPassword.text.clear()
                binding.etPhoneNum.text.clear()
                binding.etAddress.text.clear()

                binding.progressBar.visibility = View.GONE

                Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)

            }.addOnFailureListener {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }

        }

    }
}