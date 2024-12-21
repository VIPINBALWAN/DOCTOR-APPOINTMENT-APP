package com.example.doctorsappointment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class PatientLoginActivity : AppCompatActivity() {
    lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_login)

        val btnLogin = findViewById<Button>(R.id.btnDoctorLogin)
        val btnRegister = findViewById<Button>(R.id.btnDoctorRegister)
        val editTxtLEmail = findViewById<EditText>(R.id.editTxtDoctorLEmail)
        val editTxtLPassword = findViewById<EditText>(R.id.editTxtDoctorLPassword)

        user = FirebaseAuth.getInstance()
        btnRegister.setOnClickListener {
            var intent = Intent(this, PatientRegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {

            if (editTxtLEmail.text.toString() == "" || editTxtLPassword.text.toString() == "") {
                Toast.makeText(this, "Please fill in the information completely", Toast.LENGTH_LONG).show()
            } else {
                val LoginEmail = editTxtLEmail.text.toString()
                val LoginPassword = editTxtLPassword.text.toString()
                user.signInWithEmailAndPassword(LoginEmail, LoginPassword)
                    .addOnCompleteListener(PatientLoginActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, PatientHomePageActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

    }
}