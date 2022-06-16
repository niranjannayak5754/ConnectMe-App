package com.example.connectme.buy_pet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.connectme.R
import com.example.connectme.databinding.ActivityBuyPetBinding

class BuyPetActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBuyPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBuyPetBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}