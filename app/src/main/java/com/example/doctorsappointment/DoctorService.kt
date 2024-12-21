package com.example.doctorsappointment

import com.google.firebase.firestore.FirebaseFirestore

class DoctorService {
    fun getDoctors(specialty: String, callback: (List<DoctorData>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val doctorsRef = db.collection("doctors")

        doctorsRef.whereEqualTo("field", specialty).get().addOnSuccessListener { querySnapshot ->
            val doctorDataList = mutableListOf<DoctorData>()

            for (document in querySnapshot) {
                val doctorData = document.toObject(DoctorData::class.java)
                if (doctorData != null) {
                    doctorDataList.add(doctorData)
                }
            }
            callback(doctorDataList)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }
}