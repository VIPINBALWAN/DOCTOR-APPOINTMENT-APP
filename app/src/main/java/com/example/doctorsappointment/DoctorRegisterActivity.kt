package com.example.doctorsappointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location

import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class DoctorRegisterActivity : AppCompatActivity() {

    lateinit var spinnerSpecialties: Spinner
    lateinit var txtRDoctorName: EditText
    lateinit var txtRDoctorSurname: EditText
    lateinit var txtRDoctorAge: EditText
    lateinit var txtRDoctorEmail: EditText
    lateinit var txtRDoctorPassword: EditText
    lateinit var btnRDocConfirm: ImageButton
    lateinit var txtRhospitalname: EditText
    lateinit var txtRhospitaladdress: EditText
    lateinit var txtRhospitalurl: EditText


    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var DoctorName: String
    lateinit var DoctorSurname: String
    lateinit var DoctorAge: String
    lateinit var DoctorField: String
    lateinit var Hospitalname: String
    lateinit var Hospitalurl: String
    lateinit var Hospitaladdress: String
    lateinit var DoctorEmail: String
    lateinit var DoctorPassword: String
    var DoctorLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_register)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        txtRDoctorName = findViewById(R.id.txtRDoctorName)
        txtRDoctorSurname = findViewById(R.id.txtRDoctorSurname)
        txtRDoctorAge = findViewById(R.id.txtRDoctorAge)
        txtRDoctorEmail = findViewById(R.id.txtRDoctorEmail)
        txtRDoctorPassword = findViewById(R.id.txtRDoctorPassword)
        btnRDocConfirm = findViewById(R.id.btnRDocConfirm)
        spinnerSpecialties = findViewById(R.id.spinnerField)
        txtRhospitalname = findViewById(R.id.txtRhospitalname)
        txtRhospitaladdress = findViewById(R.id.txtRhospitaladdress)
        txtRhospitalurl = findViewById(R.id.txtRhospitalurl)


        val specialties = resources.getStringArray(R.array.doctor_specialties)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecialties.adapter = adapter

        btnRDocConfirm.setOnClickListener {
            DoctorName = txtRDoctorName.text.toString()
            DoctorSurname = txtRDoctorSurname.text.toString()
            DoctorAge = txtRDoctorAge.text.toString()
            DoctorField = spinnerSpecialties.selectedItem.toString()
            Hospitalname = txtRhospitalname.text.toString()
            Hospitaladdress = txtRhospitaladdress.text.toString()
            Hospitalurl = txtRhospitalurl.text.toString()
            DoctorEmail = txtRDoctorEmail.text.toString()
            DoctorPassword = txtRDoctorPassword.text.toString()

            if (!DoctorEmail.contains("@gmail")) {
                Toast.makeText(this, "Enter a valid doctor email address", Toast.LENGTH_LONG).show()
            } else if (DoctorName != "" && DoctorSurname != "" && DoctorAge != "" && DoctorField != "" && DoctorEmail != "" && DoctorPassword != "") {
                fetchLocation()
            } else {
                Toast.makeText(this, "Do not enter incomplete information", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                DoctorLocation = "${location.latitude}, ${location.longitude}"
                registerDoctor()
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_LONG).show()
                DoctorLocation ="0,0"
                registerDoctor()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerDoctor() {
        auth.createUserWithEmailAndPassword(DoctorEmail, DoctorPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User added successfully", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    val doctorData = DoctorData(
                        user!!.uid,
                        DoctorName,
                        DoctorSurname,
                        DoctorAge,
                        DoctorField,
                        DoctorEmail,
                        DoctorPassword,
                        "data:image/jpeg;base64,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",
                        Hospitalname,
                        Hospitaladdress,
                        Hospitalurl,
                        DoctorLocation
                    )
                    db.collection("doctors").document(user.email!!).set(doctorData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Doctor DocumentSnapshot successfully written!")
                        }.addOnFailureListener {
                            Log.e("Firestore", it.message.toString())
                        }
                    Log.d("doctor", doctorData.toString())
                    val intent = Intent(this@DoctorRegisterActivity, DoctorLoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fetchLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
