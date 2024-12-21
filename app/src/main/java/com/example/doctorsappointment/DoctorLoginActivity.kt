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

class DoctorLoginActivity : AppCompatActivity() {
    lateinit var btnDoctorLogin: Button
    lateinit var btnDoctorRegister: Button
    lateinit var editTxtDoctorLEmail: EditText
    lateinit var editTxtDoctorLPassword: EditText
    lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_login)

        btnDoctorLogin = findViewById(R.id.btnDoctorLogin)
        btnDoctorRegister = findViewById(R.id.btnDoctorRegister)
        editTxtDoctorLEmail = findViewById(R.id.editTxtDoctorLEmail)
        editTxtDoctorLPassword = findViewById(R.id.editTxtDoctorLPassword)



        user = FirebaseAuth.getInstance()

        btnDoctorLogin.setOnClickListener {
            if (editTxtDoctorLEmail.text.toString() == "" || editTxtDoctorLPassword.text.toString() == "") {
                Toast.makeText(this, "Please fill in the information completely", Toast.LENGTH_LONG).show()
            } else if (!editTxtDoctorLEmail.text.toString().contains("@gmail")) {
                Toast.makeText(this, "Enter a valid doctor email address", Toast.LENGTH_LONG).show()
            }else {
                val LoginEmail = editTxtDoctorLEmail.text.toString()
                val LoginPassword = editTxtDoctorLPassword.text.toString()
                user.signInWithEmailAndPassword(LoginEmail, LoginPassword)
                    .addOnCompleteListener(PatientLoginActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, DoctorHomepageActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        btnDoctorRegister.setOnClickListener {
            var intent = Intent(this, DoctorRegisterActivity::class.java)
            startActivity(intent)
        }
    }
}