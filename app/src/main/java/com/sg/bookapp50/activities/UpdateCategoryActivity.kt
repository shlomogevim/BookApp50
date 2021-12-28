package com.sg.bookapp50.activities


import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.bookapp50.databinding.ActivityUpdateCategoryBinding
import io.grpc.Context
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

class UpdateCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateCategoryBinding
    var categoryId = ""
    var categoryTxt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra(CATEGORY_DOC_ID_EXSTRA).toString()
        categoryTxt = intent.getStringExtra(CATEGORY_TEXT_EXSTRA).toString()

        binding.updaeCatTxtEt.setText(categoryTxt)

        binding.updateCatBtn.setOnClickListener {

            FirebaseFirestore.getInstance().collection(CATEGORY_REF).document(categoryId)
                .update(CATEGORY_NAME, binding.updaeCatTxtEt.text.toString())
                .addOnSuccessListener {
                    hideKeyboard()
                    finish()
                }
                .addOnFailureListener {
                    Log.d(TAGG, "Cannot update category name")
                }

        }


    }

    fun hideKeyboard() {
        val inputM = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputM.isAcceptingText) {
            inputM.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

    }
}