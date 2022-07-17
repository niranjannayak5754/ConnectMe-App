package com.example.connectme.pets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.connectme.R
import com.example.connectme.buy_pet.BuyPetActivity
import com.example.connectme.databinding.ActivityPetDetailsBinding
import com.example.connectme.utils.model.BuyPet
import com.example.connectme.utils.model.Pet
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.selects.select
import org.json.JSONObject

class PetDetailsActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPetDetailsBinding
    private lateinit var selectedItem: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedItem = intent.getParcelableExtra<Pet>("PetItem")!!
        setDataToWidgets()

        binding.btnBuyPet.setOnClickListener{
            val intent = Intent(this,BuyPetActivity::class.java)
            startActivity(intent)
        }

        binding.btnBuyPet.setOnClickListener{
            makePayment(selectedItem.price!!.toInt() *100);
        }
    }


    private fun setDataToWidgets() {
        binding.tvPetNameDetail.text = selectedItem.petName
        binding.tvPetBreedNameDetail.text = selectedItem.breedName
        (selectedItem.price+"$").also { binding.tvPetPriceDetail.text = it }
        (selectedItem.weight + "Kg").also { binding.tvPetWeightDetail.text = it }
        binding.tvPetDescriptionDetail.text = selectedItem.description
        Glide.with(this).load(selectedItem.imageUrl).into(binding.ivPetItemDetail)
    }

    private fun makePayment(amount:Int) {
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name","Connect Me")
            options.put("description","We connect you with Pets")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("amount",amount)//pass amount in currency subunits


            val prefill = JSONObject()
            prefill.put("email","")
            prefill.put("contact","")

            options.put("prefill",prefill)
            co.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
//        Toast.makeText(this, "Payment Successful: $p0", Toast.LENGTH_SHORT).show()
        Snackbar.make(findViewById(android.R.id.content), "Payment Successful", Snackbar.LENGTH_SHORT).show()
        binding.btnBuyPet.visibility = View.GONE

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failure: $p1", Toast.LENGTH_SHORT).show()
    }
}