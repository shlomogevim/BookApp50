package com.sg.bookapp50.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityCateroryAddBinding


class CateroryAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCateroryAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialop: ProgressDialog
    var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCateroryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialop = ProgressDialog(this)
        progressDialop.setTitle("Please wait ....")
        progressDialop.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
            binding.submitBtn.setOnClickListener {
                validateData()
            }


    }

    private fun validateData() {
        category = binding.categoryEt.text.toString().trim()
        if (category.isEmpty()) {
            Toast.makeText(this, "Enter Category pleace ....", Toast.LENGTH_LONG).show()
        } else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        progressDialop.show()
        val timestamp = FieldValue.serverTimestamp()
        val data = HashMap<String, Any>()
        data.put(CATEGORY_ID, "$timestamp")
        data.put(CATEGORY_NAME, category)
        data.put(CATEGORY_TIMESTAMP, FieldValue.serverTimestamp())
        data.put(CATEGORY_UID, "${firebaseAuth.currentUser?.uid}")
        FirebaseFirestore.getInstance().collection(CATEGORY_REF).add(data)
            .addOnSuccessListener {
                progressDialop.dismiss()
                Toast.makeText(this, "Adding Category ....", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {
                progressDialop.dismiss()
                Toast.makeText(
                    this,
                    "Cannot Add Category ....-> ${it.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            }
    }
}