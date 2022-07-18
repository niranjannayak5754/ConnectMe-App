package com.example.connectme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectme.buy_pet.BuyPetActivity
import com.example.connectme.databinding.ActivityCartBinding
import com.example.connectme.pets.PetDetailsActivity
import com.example.connectme.utils.adapter.CartItemClicked
import com.example.connectme.utils.adapter.CartListsAdapter
import com.example.connectme.utils.model.CartItem
import com.example.connectme.utils.model.Pet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.razorpay.Checkout
import org.json.JSONObject
import org.w3c.dom.Text

private lateinit var showTotalAmount: TextView
var totalAmount = 0

class CartActivity : AppCompatActivity(), CartItemClicked {

    private lateinit var binding: ActivityCartBinding
    private lateinit var db: FirebaseFirestore
    private val myCartItems = ArrayList<CartItem>()
    private lateinit var userId: String
    private lateinit var mAdapter: CartListsAdapter
    private lateinit var mAuth: FirebaseAuth

    companion object {
        const val DOCUMENT_ID = "2263262Niranjan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = mAuth.uid.toString()

        showTotalAmount = binding.totalPriceTv

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(MyReceiver(), IntentFilter("MyTotalAmount"))

        fetchCartItemData()
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = CartListsAdapter(this, this)
        binding.cartRecyclerView.adapter = mAdapter


        binding.buyNowBtn.setOnClickListener {
           makePayment(totalAmount * 100)
        }
    }

    private fun fetchCartItemData() {
        db.collection("Cart List").document(userId).collection("My Carts")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(this@CartActivity, "Error: $error", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {
                            val cartItem = dc.document.toObject(CartItem::class.java)
                            // adding this item to myCartItems array
                            myCartItems.add(cartItem)
                        }
                    }
                    mAdapter.updateCartItem(myCartItems)
                }
            })
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
//            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun onPaymentSuccess(p0: String?) {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Payment Successful",
            Snackbar.LENGTH_SHORT
        ).show()
        binding.buyNowBtn.visibility = View.GONE
        val msg = "Your order has been placed.\nPlease Stay Tuned"
        paymentStatus(msg)
    }

    fun onPaymentError(p0: Int, p1: String?) {
        val msg = "Payment Failed.\nTry Again"
        paymentStatus(msg)
    }

    private fun paymentStatus(msg: String){
        val intent = Intent(this, BuyPetActivity::class.java)
        intent.putExtra("MESSAGE",msg)
        startActivity(intent)
    }

    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            totalAmount = intent.getIntExtra("totalAmount", 0)
            showTotalAmount.text = "Total Amount: Rs.$totalAmount"
        }
    }

    override fun onDeleteButtonClicked(item: CartItem) {
        db.collection("Cart List").document(userId).collection("My Carts").document(item.petId.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Cart Item Removed", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    override fun onBuyNowButtonClicked(item: CartItem) {
        val amount = item.totalPetPrice!!.toInt() * 100
        makePayment(amount)
    }
}
