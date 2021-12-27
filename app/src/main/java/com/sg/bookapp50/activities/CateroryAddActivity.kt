package com.sg.bookapp50.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sg.bookapp50.R
import com.sg.bookapp50.databinding.ActivityCateroryAddBinding

class CateroryAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCateroryAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCateroryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}