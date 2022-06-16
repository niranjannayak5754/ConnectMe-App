package com.example.connectme.users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Registration : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private val db = Firebase.firestore

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE
        mAuth = FirebaseAuth.getInstance()

//  Registering a new user to my app
        binding.btnRegister.setOnClickListener {

            val firstname = binding.etFirstName.text.toString()
            val lastname = binding.etLastName.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val phone = binding.etPhoneNum.text.toString()
            val address = binding.etAddress.text.toString()

            if (firstname.isNotEmpty() && lastname.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()) {

                if (password.length < 6) {
                    Toast.makeText(this, "password must be 6 digits long", Toast.LENGTH_SHORT)
                        .show()
                    binding.etPassword.requestFocus()

                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = User(
                                    firstname,
                                    lastname,
                                    phone,
                                    address,
                                    password,
                                    username
                                )
                                registerUserInFirestore(user)
                            } else {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@Registration,
                                    "Failed: $it",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

            } else {
                Toast.makeText(this, "Fill in the empty fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUserInFirestore(user: User) {
        GlobalScope.launch (Dispatchers.IO){
            db.collection("Users").document(mAuth.uid.toString()).set(user).addOnSuccessListener {
                binding.etFirstName.text.clear()
                binding.etLastName.text.clear()
                binding.etUsername.text.clear()
                binding.etPassword.text.clear()
                binding.etPhoneNum.text.clear()
                binding.etAddress.text.clear()

                binding.progressBar.visibility = View.GONE

                Toast.makeText(this@Registration, "Account created", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@Registration, "Failed: $it", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
