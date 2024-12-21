package com.example.doctorsappointment

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var patientButton = findViewById<ImageView>(R.id.patientButton)
        var doctorButton = findViewById<ImageView>(R.id.doctorButton)

        patientButton.setOnClickListener {
            val intent = Intent(this,PatientLoginActivity::class.java)
            startActivity(intent)

        }

        doctorButton.setOnClickListener {
            val intent = Intent(this,DoctorLoginActivity::class.java)
            startActivity(intent)

        }
    }
}