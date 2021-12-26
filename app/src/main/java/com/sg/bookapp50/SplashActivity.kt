package com.sg.bookapp50

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.bookapp50.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    }
}