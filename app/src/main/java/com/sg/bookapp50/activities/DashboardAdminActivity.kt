package com.sg.bookapp50.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sg.bookapp50.R
import com.sg.bookapp50.adapters.CategoryAdapter
import com.sg.bookapp50.databinding.ActivityDashboardAdminBinding
import com.sg.bookapp50.interfaces.CategoryInterface
import com.sg.bookapp50.models.Cat


class DashboardAdminActivity : AppCompatActivity(), CategoryInterface {


    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var categoriesRef: FirebaseFirestore
    private lateinit var catAdaper: CategoryAdapter
    private var categories = ArrayList<Cat>()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        categoriesRef = FirebaseFirestore.getInstance()
        catAdaper = CategoryAdapter(categories, this)
        binding.categoriesRv.adapter = catAdaper
        binding.categoriesRv.layoutManager = LinearLayoutManager(this)

        checkUser()

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }


        })




        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            /* Toast.makeText(this,"Signing out ...",Toast.LENGTH_LONG).show()
             startActivity(Intent(this,MainActivity::class.java))
             finish()*/
        }
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, CateroryAddActivity::class.java))
        }

    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(this, "Sorry , Cannot find user", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email
            binding.subTitle.text = email
        }
    }


    override fun onResume() {
        super.onResume()
        setListener()
    }

    private fun setListener() {
        categoriesRef.collection(CATEGORY_REF)
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
                catAdaper.notifyDataSetChanged()
            }
        }
    }

    override fun categoryInterfaceListenet(category: Cat) {

        val buider = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.option_menu, null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)
        buider.setView(dialogView)
            .setNegativeButton("Cancel") { _, _ -> }
        val ad = buider.show()
        deleteBtn.setOnClickListener {
            categoriesRef.collection(CATEGORY_REF).document(category.id).delete()
                .addOnSuccessListener {
                    ad.dismiss()
                }
                .addOnFailureListener {
                    Log.d(TAGG, "Cannot delet category because -> ${it.localizedMessage}")
                }
        }

        editBtn.setOnClickListener {
            val intentToUpdate = Intent(this, UpdateCategoryActivity::class.java)
            intentToUpdate.putExtra(CATEGORY_DOC_ID_EXSTRA, category.id)
            intentToUpdate.putExtra(CATEGORY_TEXT_EXSTRA, category.categoryName)
            ad.dismiss()
            startActivity(intentToUpdate)

        }
    }
}

/* private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            /* Toast.makeText(this,"Signing out ...",Toast.LENGTH_LONG).show()
             startActivity(Intent(this,MainActivity::class.java))
             finish()*/
        }
            binding.addCategoryBtn.setOnClickListener {
                startActivity(Intent(this, CateroryAddActivity::class.java))
            }

    }

    private fun checkUser() {
        val firbaseUser = auth.currentUser
        if (firbaseUser == null) {
            Toast.makeText(this, "Sorry , Cannot find user", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val email = firbaseUser.email
            binding.subTitle.text = email
        }
    }
}*/