package com.sg.bookapp50.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.bookapp50.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
   Handler().postDelayed(Runnable {
      //startActivity(Intent(this, MainActivity::class.java))

       checkUser()

   },1000)


    }

    private fun checkUser() {
        var firebaseUser = firebaseAuth.currentUser

        //firebaseUser=null



        if (firebaseUser==null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            val uid=firebaseUser.uid
            FirebaseFirestore.getInstance().collection(USER_REF).document(uid)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        val userType = value?.get(USER_TYPE)
                        if (userType == "user") {
                            startActivity(Intent(this, DashboardUserActivity::class.java))
                            finish()
                        } else if (userType == "admin") {
                            startActivity(Intent(this,DashboardAdminActivity::class.java
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
}



/* binding.selectBtn1.setOnClickListener {
        val data=HashMap<String,Any>()
        data.put("username","Shlomo")
        data.put("lastname","Gevim")
        FirebaseFirestore.getInstance().collection("JustChecking").add(data)
            .addOnSuccessListener {
                Log.d("ff","Oreeeaa")
            }
            .addOnFailureListener {
                Log.d("ff","${it.localizedMessage}")
            }



    }
*/