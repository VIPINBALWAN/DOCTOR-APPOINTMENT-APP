package com.example.doctorsappointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientProfileActivity : AppCompatActivity() {
    lateinit var txtPName: TextView
    lateinit var txtPSurname: TextView
    lateinit var txtPAge: TextView
    lateinit var txtPEmail: TextView
    lateinit var btnDeleteAccount: Button
    lateinit var btnEditProfile: Button
    lateinit var imgPatientProfile : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        txtPName = findViewById(R.id.txtPName)
        txtPSurname = findViewById(R.id.txtPSurname)
        txtPAge = findViewById(R.id.txtPAge)
        txtPEmail = findViewById(R.id.txtPEmail)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        imgPatientProfile = findViewById(R.id.imgPatientProfilePicture)


        if (user != null) {
            db.collection("patients").document(user.email!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val client = document.toObject(PatientData::class.java)
                        if (client != null) {
                            txtPName.text = "your name : " + client.first ?: "N/A"
                            txtPSurname.text = "Your surname : " + client.last ?: "N/A"
                            txtPAge.text = "your age : " + client.age ?: "N/A"
                            txtPEmail.text = "Your email : " + client.email ?: "N/A"
                            Glide.with(this).load(client.image).into(imgPatientProfile)
                        }
                    } else {
                        Log.d("DocumentSnapshot", "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.d("get failed with ", exception.message.toString())
                }
        }
        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(
                                    "FirebaseAuth",
                                    "The user account has been deleted."
                                )
                                val db = FirebaseFirestore.getInstance()
                                db.collection("patients")
                                    .document(user.email!!)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "Firestore",
                                            "The document has been deleted successfully!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "Firestore",
                                            "Error occurred.",
                                            e
                                        )
                                    }
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.w(
                                    "Firestore",
                                    "An error occurred while deleting the user.",
                                    task.exception
                                )
                            }
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }


        btnEditProfile.setOnClickListener {
            val intent = Intent(this, PatientProfileEditActivity::class.java)



            startActivity(intent)
            finish()

        }

    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun updateData() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        val db = FirebaseFirestore.getInstance()

        db.collection("patients")
            .document(userEmail!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val patientData = document.toObject(PatientData::class.java)
                    txtPName.setText("Name: ${patientData?.first}")
                    txtPSurname.setText("Surname: ${patientData?.last}")
                    txtPAge.setText("Age: ${patientData?.age}")
                    txtPEmail.setText("Email: ${patientData?.email}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting client data: ${e.message}", e)
            }
    }


}
