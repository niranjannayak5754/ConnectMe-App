package com.example.connectme.pets

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.crop_image.ImageCrop
import com.example.connectme.databinding.ActivityAddPetBinding
import com.example.connectme.utils.model.CategoryData
import com.example.connectme.utils.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        binding.ivAddPet.setOnClickListener {
            resultLauncher.launch("image/*")
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
                val imageReference = storage.reference.child(System.currentTimeMillis().toString() + ".any")
                imageReference.putFile(uri!!).addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener {
                        val pet = Pet(petName, breedName,it.toString(), price, weight, description)
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
        db.collection("Pet").document(mAuth.uid.toString()).collection(pet.petName.toString())
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

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
//            binding.ivAddPet.setImageURI(it)
//            uri = it
            val intent = Intent(this, ImageCrop::class.java)
            intent.putExtra("KEY",it.toString())
            startActivityForResult(intent,101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==-1 && requestCode==101){
            val result = data!!.getStringExtra("Result").toString()
            uri =Uri.parse(result)
            binding.ivAddPet.setImageURI(uri)
        }
    }
}