package com.sg.bookapp50.activities

import android.app.AlertDialog
import android.app.Application
import android.app.Instrumentation
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityPdfAddBinding
import com.sg.bookapp50.models.Cat
import java.lang.reflect.Field

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progress: ProgressDialog
    private lateinit var categories: ArrayList<Cat>
    private var pdfUri: Uri? = null
    private val TAG = "pdf"
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        categories = ArrayList()
        progress = ProgressDialog(this)
        progress.setTitle("Please wait ...")
        progress.setCanceledOnTouchOutside(false)

        loadCategories()

        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.attachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }
        binding.submiteBtn.setOnClickListener {
            validateData()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        Log.d(TAG, "validationData")
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title please ...", Toast.LENGTH_LONG).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Description please ...", Toast.LENGTH_LONG).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Pick Category please ...", Toast.LENGTH_LONG).show()
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick PDF please ...", Toast.LENGTH_LONG).show()

        } else {
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        Log.d(TAG, "Uploading to storage ...")
        progress.setMessage("uploadind PDF ...")
        progress.show()

        // val timestamp = FieldValue.serverTimestamp()
        val timestamp = System.currentTimeMillis()
        val filePathAndName = "Book/$timestamp"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnap ->
                Log.d(TAG, "UploadPDFto storage ")
                val uriTask: Task<Uri> = taskSnap.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploaderPdfUrl = "${uriTask.result}"
                uploadPdfInfoToDb(uploaderPdfUrl, timestamp)


            }
            .addOnFailureListener {
                Log.d(TAG, "Fail in uploadPDFto storage --> ${it.localizedMessage}")
                progress.dismiss()
                Toast.makeText(this, "ail in uploadPDFto storage", Toast.LENGTH_LONG).show()

            }


    }

    private fun uploadPdfInfoToDb(uploaderPdfUrl: String, timestamp: Long) {
        Log.d(TAG, "Uploading to db")


        progress.setMessage("Uploadiding pdf to firestore")
        val uid = firebaseAuth.uid
        val haseMap: HashMap<String, Any> = HashMap()

        val time = FieldValue.serverTimestamp()
        val time1 = System.currentTimeMillis()

        haseMap["uid"] = "$uid"
        haseMap["id"] = "$timestamp"
        haseMap["title"] = title
        haseMap["description"] = description
        haseMap["categoryId"] = selectedCategoryId
        haseMap["url"] = uploaderPdfUrl
        haseMap["timesample"] = time
        haseMap["viewcount"] = 0
        haseMap["downloadCount"] = 0

        FirebaseFirestore.getInstance().collection(BOOK_REF).document("$time1")
            .set(haseMap)
            .addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(this, "Upload PDF", Toast.LENGTH_LONG).show()
                pdfUri = null
            }
            .addOnFailureListener {
                progress.dismiss()
                Toast.makeText(this, "Cannot upload PDF", Toast.LENGTH_LONG).show()

            }


    }




    private fun categoryPickDialog() {
        val categoriesArray = arrayOfNulls<String>(categories.size)
        for (i in categoriesArray.indices) {
            categoriesArray[i] = categories[i].categoryName
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Categpry please ...")
            .setItems(categoriesArray) { dialog, which ->
                selectedCategoryId = categories[which].id
                selectedCategoryTitle = categories[which].categoryName
                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG, " selecte category Id --> $selectedCategoryId")
                Log.d(TAG, " selecte category Title --> $selectedCategoryTitle")
            }.show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickingIntent: starting pdf pick intent")
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLancher.launch(intent)
    }

    val pdfActivityResultLancher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF  picked")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF  canceled")
                Toast.makeText(this, "PDF canceled", Toast.LENGTH_LONG).show()
            }
        }

    )

    private fun loadCategories() {
        setListener()
    }
    private fun setListener() {
        FirebaseFirestore.getInstance().collection(CATEGORY_REF)
            .orderBy(CATEGORY_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener(this) { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(
                        this,
                        "Cannot download data->${exception.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (snapshot != null) {
                        parshData(snapshot)
                    }
                }

            }
    }

    private fun parshData(snapshot: QuerySnapshot) {
        var categoryName = ""
        categories.clear()
        for (document in snapshot) {
            val data = document.data
            if (data != null) {
                if (data[CATEGORY_NAME] != null) {
                    categoryName = data[CATEGORY_NAME] as String
                    val timestamp = document.getTimestamp(CATEGORY_TIMESTAMP)
                    val id = document.id
                    val newCat = Cat(id, categoryName, timestamp)
                    categories.add(newCat)
                }
                //catAdaper.notifyDataSetChanged()
            }
        }
    }
}