package com.sg.bookapp50.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please wait ....")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener {
            validateData()
        }


    }

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email =binding.emailEt.text.toString()
        password = binding.passwordEt.text.toString().trim()
        val cPpassword =binding.cPpasswordEt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name please ....", Toast.LENGTH_LONG).show()
        } else {
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email please ....", Toast.LENGTH_LONG).show()

            } else {
                if (password.isEmpty()) {
                    Toast.makeText(this, "Enter your password ", Toast.LENGTH_LONG).show()
                } else
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Invalid Email ....", Toast.LENGTH_LONG).show()

                    } else {
                        if (cPpassword.isEmpty()) {
                            Toast.makeText(this, "Confirm password", Toast.LENGTH_LONG).show()
                        } else {
                            if (password != cPpassword) {
                                Toast.makeText(this, "Password is not match", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                createUserAccount()
                            }
                        }
                    }
            }
        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating Account ...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val changeRequest=UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                result.user?.updateProfile(changeRequest)
                    ?.addOnSuccessListener {
                        Log.d(TAGG," Sucsess in changing profile")
                    }
                    ?.addOnFailureListener {
                        Log.d(TAGG," FAIL in changing profile -> ${it.localizedMessage}")

                    }

                updateUserInfo()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.d(TAGG," FAIL in create user account -> ${it.localizedMessage}")
            }
    }

    private fun updateUserInfo() {
        progressDialog.setTitle("Saving use information to FireStore....")

       // val uid=firebaseAuth.uid
        val uid=firebaseAuth.currentUser?.uid

        val data=HashMap<String,Any>()
        data.put(USER_NAME,name)
        data.put(USER_EMAIL,email)
        uid?.let { data.put(USER_UID, it) }
        data.put(USER_PASSWORD,password)
        data.put(USER_PROFILE_IMAGE,"")
        data.put(USER_TYPE,"user")
        data.put(USER_DATE_CREATE,FieldValue.serverTimestamp())

        uid?.let {
            FirebaseFirestore.getInstance().collection(USER_REF).document(it)
                .set(data)
                .addOnSuccessListener {

                    Log.d(TAGG," Sucsess in creating profile")
                }
                .addOnFailureListener {
                    Log.d(TAGG," FAIL in create user account2 -> ${it.localizedMessage}")
                }
            progressDialog.dismiss()
        }

        startActivity(Intent(this,DashboardUserActivity::class.java))
        finish()
    }
}


/*private fun createUserAccount() {
        progressDialog.setMessage("Creating Account ...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Cannot create User Acount1 -->${it.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()


            }
    }

    private fun updateUserInfo() {
        progressDialog.setTitle("Saving use information....")
        val timeStamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        var hasMap: HashMap<String, Any?> = HashMap()
        hasMap["name"] = name
        hasMap["uid"] = uid
        hasMap["email"] = email
        hasMap["password"] = password
        hasMap["profileImage"] = ""
        hasMap["userType"] = "user"
        hasMap["timestamp"] = timeStamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hasMap)
            .addOnSuccessListener { input ->
                progressDialog.dismiss()
                Toast.makeText(this, "Create User Acount -->${input.toString()}", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, DashboardUserActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Cannot saving User Acount -->${it.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            }


    }*/