package com.example.connectme.pets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectme.databinding.ActivityProfileBinding
import com.example.connectme.utils.adapter.PetMenuAdapter
import com.example.connectme.utils.adapter.PetsItemClicked
import com.example.connectme.utils.model.CategoryData
import com.example.connectme.utils.model.Pet
import com.google.firebase.firestore.*

class ProfileActivity : AppCompatActivity(), PetsItemClicked {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var petsCategoryList: ArrayList<CategoryData>
    private lateinit var adapter: PetMenuAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddPet.setOnClickListener {
            val intent = Intent(this, AddPetActivity::class.java)
            startActivity(intent)
        }

        binding.petItemRecyclerView.layoutManager = LinearLayoutManager(this@ProfileActivity,LinearLayoutManager.HORIZONTAL,false)
//      binding.petItemRecyclerView.setHasFixedSize(true)
        fetchData()
        adapter = PetMenuAdapter(this@ProfileActivity)
        binding.petItemRecyclerView.adapter = adapter
    }

    private fun fetchData() {
        petsCategoryList = ArrayList<CategoryData>()
        db = FirebaseFirestore.getInstance()
        db.collection("Pet Categories")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Toast.makeText(this@ProfileActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        petsCategoryList.add(dc.document.toObject(CategoryData::class.java))
                    }
                }
                adapter.updateNews(petsCategoryList)
            }
        })
    }

    override fun onItemClicked(category: String) {
        val intent = Intent(this, PetCollectionActivity::class.java)
        intent.putExtra("category",category)
        startActivity(intent)
    }
}