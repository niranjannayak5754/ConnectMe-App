package com.example.connectme.pets

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.connectme.CartActivity
import com.example.connectme.MainActivity
import com.example.connectme.R
import com.example.connectme.databinding.ActivityPetCollectionBinding
import com.example.connectme.utils.adapter.PetDetailsAdapter
import com.example.connectme.utils.adapter.PetDetailsItemClicked
import com.example.connectme.utils.model.Pet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class PetCollectionActivity : AppCompatActivity(), PetDetailsItemClicked {

       companion object{
           const val DOCUMENT_ID = "2263262Niranjan"
       }
        private lateinit var binding: ActivityPetCollectionBinding
        private  var mAuth = FirebaseAuth.getInstance()
        private lateinit var petsList: ArrayList<Pet>
        private  lateinit var db: FirebaseFirestore
        private lateinit var mAdapter: PetDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category: String = intent.getStringExtra("category").toString()

        binding.mainRecyclerView.layoutManager = GridLayoutManager(this,2)
        fetchData(category)
        mAdapter = PetDetailsAdapter(this@PetCollectionActivity)
        binding.mainRecyclerView.adapter = mAdapter

        binding.cartIv.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchData(category: String) {
        petsList = ArrayList()
        db = FirebaseFirestore.getInstance()

        db.collection("Pet").document(DOCUMENT_ID).collection(category)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(this@PetCollectionActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val petItem = dc.document.toObject(Pet::class.java)
                            petItem.petId = dc.document.id
                            petsList.add(petItem)
                        }
                    }
                    mAdapter.updatePets(petsList)
                }
            })
    }

    override fun onItemClicked(item: Pet) {
        val intent = Intent(this, PetDetailsActivity::class.java)
        intent.putExtra("PetItem",item)
        startActivity(intent)
    }
}