package com.example.connectme.buy_pet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.connectme.R
import com.example.connectme.databinding.ActivityBuyPetBinding
import com.example.connectme.pets.ProfileActivity

class BuyPetActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBuyPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBuyPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val msg = intent.getStringExtra("MESSAGE")
        binding.orderStatusTextView.text = msg

        binding.getBackBtn.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        fun onBackPressed(){
            // do nothing
        }

    }
}