package com.example.connectme.pets

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connectme.MainActivity
import com.example.connectme.R
import com.example.connectme.databinding.ActivityProfileBinding
import com.example.connectme.users.User
import com.example.connectme.utils.adapter.PetMenuAdapter
import com.example.connectme.utils.adapter.PetsItemClicked
import com.example.connectme.utils.model.CategoryData
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity(), PetsItemClicked {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: User
    private lateinit var storage: FirebaseStorage
    private lateinit var petsCategoryList: ArrayList<CategoryData>
    private lateinit var adapter: PetMenuAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        showProfileInfo()

        binding.profilePicture.setOnClickListener {
            ImagePicker.with(this)
                .provider(ImageProvider.BOTH)
                .crop()
                .maxResultSize(720, 720)
                .createIntentFromDialog {
                    resultLauncher.launch(it)
                }
        }

        binding.fabAddPet.setOnClickListener {
            val intent = Intent(this, AddPetActivity::class.java)
            startActivity(intent)
        }

        binding.editProfileBtn.setOnClickListener {
            showCustomAlertDialog()
        }

        binding.profileLogout.setOnClickListener {
            val title = "Log Out"
            val msg = "Are you sure?"
            alertDialogBox(title, msg)
        }

        binding.petItemRecyclerView.layoutManager =
            LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.HORIZONTAL, false)
        fetchData()
        adapter = PetMenuAdapter(this@ProfileActivity)
        binding.petItemRecyclerView.adapter = adapter
    }

    private fun alertDialogBox(title: String, msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("\t\t $title")
        builder.setMessage("\t\t $msg")
        builder.setPositiveButton("YES"){ _, _ ->
            mAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
        builder.setNegativeButton("NO"){ _, _ ->
            // empty body
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


    }

    private fun showCustomAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = LayoutInflater.from(this).inflate(R.layout.edit_profile_layout, null)

        val fNameEt: EditText = customLayout.findViewById(R.id.fNameET)
        val lNameEt: EditText = customLayout.findViewById(R.id.lNameET)
        val addressEt: EditText = customLayout.findViewById(R.id.addressET)
        val phoneEt: EditText = customLayout.findViewById(R.id.phoneET)

        fNameEt.setText(user.firstname)
        lNameEt.setText(user.lastname)
        addressEt.setText(user.address)
        phoneEt.setText(user.phone)

        alertDialog.setView(customLayout)
        alertDialog.setTitle("Edit Profile")

        alertDialog.setPositiveButton("Update") { _, _ ->
            val editedFirstName = fNameEt.text.toString()
            val editedLastName = lNameEt.text.toString()
            val editedPhone = phoneEt.text.toString()
            val editedAddress = addressEt.text.toString()

            user.firstname = editedFirstName
            user.lastname = editedLastName
            user.address = editedAddress
            user.phone = editedPhone

            changeData()
        }

        alertDialog.setNeutralButton("Cancel") { _, _ ->
            // nothing to perform
        }
        val alert = alertDialog.create()
        alert.setCancelable(false)
        alert.show()
    }

    private fun showProfileInfo() {

        val docRef = db.collection("Users").document(mAuth.uid.toString())
        docRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    Log.d("USER_DETAILS", "${it.data}")
                    user = it.toObject(User::class.java)!!
                    showProfileData()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
                Log.d("USER_DETAILS", "Failure in fetching")
            }
    }

    private fun showProfileData() {
        binding.profileUsername.text = user.username
        (user.firstname + " " + user.lastname).also { binding.profileName.text = it }
        binding.profilePhone.text = user.phone
        binding.profileAddress.text = user.address
        val url = user.profileImageUrl
        if (url != null) {
            Glide.with(this)
                .load(url)
                .into(binding.profilePicture)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                binding.profilePicture.setImageURI(uri)
                uploadProfilePic(uri)
            }
        }

    private fun uploadProfilePic(uri: Uri) {
        val imageReference = storage.reference.child("Users/${mAuth.uid}/"+System.currentTimeMillis().toString()+ ".jpeg")
        imageReference.putFile(uri).addOnSuccessListener {
            imageReference.downloadUrl.addOnSuccessListener {
                user.profileImageUrl = it.toString()
                changeData()
            }
        }
    }

    private fun changeData() {
        db.collection("Users").document(mAuth.uid.toString())
            .set(user).addOnSuccessListener {
                showProfileInfo()
            }.addOnFailureListener {
                Toast.makeText(this, "Blunder God", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        petsCategoryList = ArrayList<CategoryData>()
        db.collection("Pet Categories")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(this@ProfileActivity, "Error: $error", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            petsCategoryList.add(dc.document.toObject(CategoryData::class.java))
                        }
                    }
                    adapter.updatePetsCategory(petsCategoryList)
                }
            })
    }

    override fun onItemClicked(category: String) {
        val intent = Intent(this, PetCollectionActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}