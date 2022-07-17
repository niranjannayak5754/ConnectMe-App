package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.databinding.ActivityMainBinding
import com.example.connectme.pets.PetCollectionActivity
import com.example.connectme.pets.ProfileActivity
import com.example.connectme.users.Registration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tag = "FireBase Activity: "

    //    firebase Authentication
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE
        actionBar?.show()

        mAuth = FirebaseAuth.getInstance()

//  This takes to the registration activity for new users
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

//  This allows user to login if credentials are correct
        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(this, "Fill in the empty fields", Toast.LENGTH_SHORT).show()
                if (email.isEmpty())
                    binding.etUsername.requestFocus()
                else
                    binding.etPassword.requestFocus()
            }
        }

        binding.btnGoogleSignIn.setOnClickListener{
            Snackbar.make(it,"Feature Coming Soon",Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}