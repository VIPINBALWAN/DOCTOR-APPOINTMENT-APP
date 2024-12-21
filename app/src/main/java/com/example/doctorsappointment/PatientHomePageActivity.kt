package com.example.doctorsappointment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientHomePageActivity : AppCompatActivity() {

    lateinit var doctorsListView: ListView
    lateinit var doctorsAdapter: DoctorCustomAdapter
    lateinit var doctorService: DoctorService
    val doctorsList = mutableListOf<DoctorData>()
    lateinit var userImage: String
    lateinit var userName: String
    var userLocation: Location? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home_page)

        doctorsListView = findViewById(R.id.listView)
        doctorService = DoctorService()
        doctorsAdapter = DoctorCustomAdapter(this, doctorsList)
        doctorsListView.adapter = doctorsAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationAndFetchDoctors()

        val specialtySpinner = findViewById<Spinner>(R.id.spSpecialty)
        specialtySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchDoctors()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            db.collection("patients").document(userEmail).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        userImage = documentSnapshot.getString("image") ?: ""
                        val name = documentSnapshot.getString("first") ?: ""
                        val surname = documentSnapshot.getString("last") ?: ""
                        userName = "$name $surname"
                    }
                }
        }

        doctorsListView.setOnItemClickListener { adapterView, view, i, l ->
            val selectedItem = adapterView.getItemAtPosition(i) as DoctorData
            Log.d("info", selectedItem.toString())

            val intent = Intent(this, AppointmentActivity::class.java)
            intent.putExtra("name", selectedItem.first)
            intent.putExtra("hospital name", selectedItem.hospitalname)
            intent.putExtra("hospital address", selectedItem.hospitaladdress)
            intent.putExtra("hospital url", selectedItem.hospitalurl)
            intent.putExtra("surname", selectedItem.last)
            intent.putExtra("age", selectedItem.age)
            intent.putExtra("field", selectedItem.field)
            intent.putExtra("image", selectedItem.image)
            intent.putExtra("email", selectedItem.email)
            intent.putExtra("patientImage", userImage)
            intent.putExtra("patientName", userName)
            startActivity(intent)
        }
    }

    fun getLocationAndFetchDoctors() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                userLocation = location
                fetchDoctors()
            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Location", "Failed to get location", it)
        }
    }

    fun fetchDoctors() {
        val selectedSpecialty = findViewById<Spinner>(R.id.spSpecialty).selectedItem.toString()

        doctorService.getDoctors(selectedSpecialty) { doctorDataList ->
            doctorsList.clear()
            for (doctor in doctorDataList) {
                val doctorLocation = Location("")
                val locationArray = doctor.address?.split(",")
                if (locationArray != null && locationArray.size == 2) {
                    doctorLocation.latitude = locationArray[0].toDouble()
                    doctorLocation.longitude = locationArray[1].toDouble()
                }

                val distance = userLocation?.distanceTo(doctorLocation) ?: 0f
                doctor.distance = distance

                doctorsList.add(doctor)
            }

            doctorsList.sortBy { it.distance }
            doctorsAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.patient_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this, PatientProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Log out of account")
                    setMessage("Are you sure you want to log out?")
                    setPositiveButton("Yes") { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton("No", null)
                }.create().show()
            }
            R.id.appointments -> {
                val intent = Intent(this, PatientMyAppointmentsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndFetchDoctors()
        }
    }

    override fun onResume() {
        super.onResume()

}

}
