package com.example.connectme.crop_image

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connectme.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class ImageCrop : AppCompatActivity() {

    private lateinit var fileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)
        readIntent()

        val destUri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        val options = UCrop.Options()
        UCrop.of(fileUri, Uri.fromFile(File(cacheDir, destUri)))
            .withOptions(options)
            .withAspectRatio(0F, 0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000)
            .start(this)
    }

    private fun readIntent() {
        val result = intent.getStringExtra("KEY").toString()
        fileUri = Uri.parse(result)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            val returnIntent = Intent()
            returnIntent.putExtra("Result", resultUri.toString())
            setResult(-1, returnIntent)
            finish()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error fetching uri", Toast.LENGTH_SHORT).show()
        }
    }
}