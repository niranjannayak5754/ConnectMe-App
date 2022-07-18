package com.example.connectme.pets

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.databinding.ActivityAddPetBinding
import com.example.connectme.utils.model.CategoryData
import com.example.connectme.utils.model.Pet
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPetActivity : AppCompatActivity() {

    companion object{
        const val DOCUMENT_ID = "2263262Niranjan"
    }

    private lateinit var binding: ActivityAddPetBinding
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private var uri: Uri? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.progressBar.visibility = View.GONE

        binding.ivAddPet.setOnClickListener {
            ImagePicker.with(this)
                .provider(ImageProvider.BOTH)
                .crop()
                .maxResultSize(720,720)
                .createIntentFromDialog {
                    resultLauncher.launch(it)
                }
        }

        binding.btnAddPet.setOnClickListener {
            val petName = binding.etPetName.text.toString()
            val breedName = binding.etBreedName.text.toString()
            val price = binding.etPrice.text.toString()
            val weight = binding.etWeight.text.toString()
            val description = binding.etDescriptioin.text.toString()
            if (uri != null && petName.isNotEmpty() && breedName.isNotEmpty() && price.isNotEmpty() && weight.isNotEmpty() && description.isNotEmpty()) {
                binding.btnAddPet.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                // uploading the pet image in the firebase storage and downloading the url of that file
                val imageReference = storage.reference.child("Pets/${mAuth.uid}/"+System.currentTimeMillis().toString() + ".jpeg")
                imageReference.putFile(uri!!).addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener {
                        val pet = Pet("xxx",petName, breedName,it.toString(), price, weight, description)
                        val data = CategoryData(petName,it.toString())
                        addCategory(data)
                        addPet(pet)
                    }.addOnFailureListener{
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnAddPet.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.progressBar.visibility = View.GONE
                if (uri == null)
                    Toast.makeText(this@AddPetActivity, "Select Photo", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(
                        this@AddPetActivity,
                        "Fill in the empty fields first",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun addCategory(data: CategoryData) {
        db.collection("Pet Categories").document(data.petName.toString())
            .set(data).addOnSuccessListener {
                Toast.makeText(this, "new pet category added", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addPet(pet: Pet) {
        db.collection("Pet").document(DOCUMENT_ID).collection(pet.petName.toString())
            .document(UUID.randomUUID().toString())
            .set(pet).addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AddPetActivity, "Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@AddPetActivity, "Failed: $it", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK)
        {
            uri = it.data?.data!!
            binding.ivAddPet.setImageURI(uri)

        }
    }
}