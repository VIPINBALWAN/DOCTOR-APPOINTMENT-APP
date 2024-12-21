package com.example.doctorsappointment

import com.google.firebase.firestore.FirebaseFirestore

class DoctorAppointmentService {
    private val db = FirebaseFirestore.getInstance()

    fun getAppointmentsForDoctor(
        doctorEmail: String,
        callback: (List<DoctorAppointmentData>) -> Unit
    ) {
        db.collection("doctorAppointments")
            .document(doctorEmail)
            .collection("appointments")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(DoctorAppointmentData::class.java)
                    appointment?.copy(id = document.id)
                }
                callback(appointmentsList)
            }
    }

    fun deleteAppointment(doctorEmail: String, patientEmail: String, appointmentId: String, callback: (Boolean) -> Unit) {
        db.collection("doctorAppointments")
            .document(doctorEmail)
            .collection("appointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener {
                db.collection("appointments")
                    .document(patientEmail)
                    .collection("patientAppointments")
                    .document(appointmentId)
                    .delete()
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
