package com.example.connectme.pets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connectme.R
import com.example.connectme.buy_pet.BuyPetActivity
import com.example.connectme.databinding.ActivityPetDetailsBinding
import com.example.connectme.utils.model.CartItem
import com.example.connectme.utils.model.Pet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class PetDetailsActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPetDetailsBinding
    private lateinit var selectedItem: Pet
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private val cartItem = CartItem()
    private var selectedQuantity = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedItem = intent.getParcelableExtra<Pet>("PetItem")!!
        setDataToWidgets()

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        binding.btnBuyPet.setOnClickListener {
            makePayment(selectedItem.price!!.toInt() * 100);
        }

        binding.quantitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedQuantity = p0!!.getItemAtPosition(p2).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
            Snackbar.make(it, "Added to Cart", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun addToCart() {
//        getting user_id, pet_id, quantity and total_price for storing in the database in cart lists collection
        val userId = mAuth.uid.toString()

        cartItem.petId = selectedItem.petId
        cartItem.petName = selectedItem.petName
        cartItem.breedName = selectedItem.breedName
        cartItem.price = selectedItem.price
        cartItem.quantity = selectedQuantity
        cartItem.imageUrl = selectedItem.imageUrl
        cartItem.totalPetPrice = selectedItem.price

        db.collection("Cart List").document(userId).collection("My Carts")
            .document(cartItem.petId.toString())
            .set(cartItem).addOnSuccessListener {
                // empty body
            }.addOnFailureListener {
                Toast.makeText(this, "Failed!!! Check your connection", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setDataToWidgets() {
        binding.tvPetNameDetail.text = selectedItem.petName
        binding.tvPetBreedNameDetail.text = selectedItem.breedName
        ("Rs. " + selectedItem.price).also { binding.tvPetPriceDetail.text = it }
        (selectedItem.weight + "Kg").also { binding.tvPetWeightDetail.text = it }
        binding.tvPetDescriptionDetail.text = selectedItem.description
        Glide.with(this).load(selectedItem.imageUrl).into(binding.ivPetItemDetail)
    }

    private fun makePayment(amount: Int) {
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Connect Me")
            options.put("description", "We connect you with Pets")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount)//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email", "")
            prefill.put("contact", "")

            options.put("prefill", prefill)
            co.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
//        Toast.makeText(this, "Payment Successful: $p0", Toast.LENGTH_SHORT).show()
        Snackbar.make(
            findViewById(android.R.id.content),
            "Payment Successful",
            Snackbar.LENGTH_SHORT
        ).show()
        binding.btnBuyPet.visibility = View.GONE
        val msg = "Your order has been placed.\nPlease Stay Tuned"
        paymentStatus(msg)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        val msg = "Payment Failed.\nTry Again"
       paymentStatus(msg)
    }

    private fun paymentStatus(msg: String){
        val intent = Intent(this,BuyPetActivity::class.java)
        intent.putExtra("MESSAGE",msg)
        startActivity(intent)
    }
}