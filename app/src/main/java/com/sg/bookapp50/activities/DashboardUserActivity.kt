package com.sg.bookapp50.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
           /* startActivity(Intent(this,MainActivity::class.java))
            finish()*/
            checkUser()
        }

    }

    private fun checkUser() {
        val firbaseUser=firebaseAuth.currentUser
        if (firbaseUser==null){
            binding.subTitle.text="Not Logged In ..."
        }else{
            val email=firbaseUser.email
            binding.subTitle.text=email
        }
    }
}