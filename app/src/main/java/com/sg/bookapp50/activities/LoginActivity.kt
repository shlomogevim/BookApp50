package com.sg.bookapp50.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityLoginBinding
import com.sg.bookappfirebase.TAGG
import com.sg.bookappfirebase.USER_REF
import com.sg.bookappfirebase.USER_TYPE

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    var email = ""
    var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait ...")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Wrong email format...", Toast.LENGTH_LONG).show()
        } else {
            if (password.isEmpty()) {
                Toast.makeText(this, "password is empty...", Toast.LENGTH_LONG).show()
            } else {
                loginUser()
            }
        }

    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In ....")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener {
                Toast.makeText(this, "cannot Sign in because:->${it.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User ....")
        val firebaseUser = firebaseAuth.currentUser!!
        val uid=firebaseUser.uid
         FirebaseFirestore.getInstance().collection(USER_REF).document(uid)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    progressDialog.dismiss()
                    val userType = value?.get(USER_TYPE)
                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    } else if (userType == "admin") {
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DashboardAdminActivity::class.java
                            )
                        )
                        finish()
                    }
                }
                else{
                    Log.d( TAGG,"Cannot enter to user account")
                }
            }
    }

}



       /* ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType=snapshot.child("userType").value
                    if (userType=="user"){
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }else if (userType=="admin"){
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }
}*/