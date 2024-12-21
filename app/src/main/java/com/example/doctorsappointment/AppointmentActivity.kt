package com.example.doctorsappointment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.Calendar
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.net.Uri


class AppointmentActivity : AppCompatActivity() {
    companion object {
        val CHANNEL_ID = "my_channel"
        val NOTIFICATION_ID = 1
    }
    private lateinit var txtAppName: TextView
    private lateinit var txtAppSurname: TextView
    private lateinit var txtAppAge: TextView
    private lateinit var txtAppField: TextView
    private lateinit var btnSelectDate: ImageButton
    private lateinit var hospitalname: TextView
    private lateinit var hospitaladdress: TextView
    private lateinit var hospitalurl: TextView

    private lateinit var btnMakeApp: Button
    private lateinit var editTxtAppNote: EditText
    private lateinit var ImgApp: ImageView


    private lateinit var btn9am: Button
    private lateinit var btn10am: Button
    private lateinit var btn11am: Button
    private lateinit var btn12pm: Button
    private lateinit var btn2pm: Button
    private lateinit var btn3pm: Button
    private lateinit var btn4pm: Button
    private lateinit var btn5pm: Button

    var previouslyClickedButton: Button? = null
    var Date = ""
    var selectedHour = ""

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        txtAppName = findViewById(R.id.txtAppName)
        txtAppSurname = findViewById(R.id.txtAppSurname)
        txtAppAge = findViewById(R.id.txtAppAge)
        txtAppField = findViewById(R.id.txtAppField)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        ImgApp = findViewById(R.id.ImgApp)
        btnMakeApp = findViewById(R.id.btnMakeApp)
        editTxtAppNote = findViewById(R.id.editTxtAppNote)
        hospitalname = findViewById(R.id.hospitalname)
        hospitaladdress = findViewById((R.id.hospitaladdress))
        hospitalurl = findViewById((R.id.hospitalurl))

        btn9am = findViewById(R.id.btn_9am)
        btn10am = findViewById(R.id.btn_10am)
        btn11am = findViewById(R.id.btn_11am)
        btn12pm = findViewById(R.id.btn_12pm)
        btn2pm = findViewById(R.id.btn_2pm)
        btn3pm = findViewById(R.id.btn_3pm)
        btn4pm = findViewById(R.id.btn_4pm)
        btn5pm = findViewById(R.id.btn_5pm)

        val doctorName = intent.getStringExtra("name")
        val doctorSurname = intent.getStringExtra("surname")
        val doctorAge = intent.getStringExtra("age")
        val doctorField = intent.getStringExtra("field")
        val doctorImage = intent.getStringExtra("image")
        val hospitaname = intent.getStringExtra("hospital name")
        val hospitaaddress = intent.getStringExtra("hospital address")
        val hospitaurl = intent.getStringExtra("hospital url")
        val patientImage = intent.getStringExtra("patientImage")
        val patientFullName = intent.getStringExtra("patientName")

        txtAppName.text = "Name : " + doctorName
        txtAppSurname.text = "Surname : " + doctorSurname
        txtAppAge.text = "Age: " + doctorAge
        txtAppField.text = "Area : " + doctorField
        hospitalname.text = "Hospital name : " + hospitaname
        hospitaladdress.text = "Hospital address : " + hospitaaddress
        hospitalurl.text = hospitaurl
        Glide.with(this).load(doctorImage).into(ImgApp)


        hospitalurl.setOnClickListener{
            openLocationInGoogleMaps(hospitaurl)
        }


        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->


                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)


                if (dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(this, "There is no work on Sundays, cannot be selected", Toast.LENGTH_LONG)
                        .show()
                } else {
                    var ay = "${selectedMonth + 1}"
                    if (selectedMonth + 1 < 10) {
                        ay = "0${selectedMonth + 1}"
                    }

                    Date = "$selectedDayOfMonth.$ay.$selectedYear"
                    val new=findViewById<TextView>(R.id.selecteddate)
                    new.text=Date
                    fetchBookedAppointmentsForDate(Date)
                }

            },
            year,
            month,
            dayOfMonth
        )

        val minDate = Calendar.getInstance()
        minDate.add(Calendar.DAY_OF_MONTH, 0)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_MONTH, 20)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        btnSelectDate.setOnClickListener {
            datePickerDialog.show()
        }

        if (Date.isEmpty()) {
            Toast.makeText(this, "Please select date first", Toast.LENGTH_LONG).show()
        }
        btn9am.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn9am
                selectedHour= btn9am.text.toString()
                btn9am.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn10am.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn10am
                selectedHour= btn10am.text.toString()
                btn10am.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn11am.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn11am
                selectedHour= btn11am.text.toString()
                btn11am.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn12pm.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn12pm
                selectedHour= btn12pm.text.toString()
                btn12pm.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn2pm.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn2pm
                selectedHour= btn2pm.text.toString()
                btn2pm.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn3pm.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn3pm
                selectedHour= btn3pm.text.toString()
                btn3pm.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn4pm.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn4pm
                selectedHour= btn4pm.text.toString()
                btn4pm.setBackgroundColor(resources.getColor(R.color.teal_200))
            }
            btn5pm.setOnClickListener{
                if (previouslyClickedButton != null) { previouslyClickedButton!!.setBackgroundColor(resources.getColor(R.color.purple)) }
                previouslyClickedButton = btn5pm
                selectedHour= btn5pm.text.toString()
                btn5pm.setBackgroundColor(resources.getColor(R.color.teal_200))
            }

        createNotificationChannel()
        btnMakeApp.setOnClickListener {
            val patientEmail = FirebaseAuth.getInstance().currentUser?.email
            val doctorEmail = intent.getStringExtra("email")
            val patientImage = patientImage
            val doctorImage = doctorImage
            val appointmentNote = editTxtAppNote.text.toString()
            val appointmentDate = Date
            val appointmentHour = selectedHour

            if (patientEmail != null && appointmentDate.isNotEmpty() && appointmentHour.isNotEmpty()) {
                val doctorFullname = doctorName + " " + doctorSurname
                val appointmentInfo = AppointmentData(
                    null,
                    doctorEmail,
                    patientEmail,
                    patientFullName,
                    patientImage,
                    doctorFullname,
                    doctorImage,
                    doctorField,
                    appointmentNote,
                    appointmentDate,
                    appointmentHour
                )
                addAppointmentToFirestore(patientEmail,doctorEmail!!,appointmentInfo)
                Toast.makeText(this, "Your appointment has been created successfully", Toast.LENGTH_LONG).show()
                val timeRange = appointmentHour
                val startTime = timeRange.split("-")[0] // Extract the start time
                val timeParts = startTime.split(":") // Split the time into hours and minutes
                val hour = timeParts[0].toInt() // Convert the hour to an integer
                val minute = timeParts[1].substring(0, 2).toInt() // Convert the minute to an integer
                val amPm = timeParts[1].substring(2) // Get the AM/PM part

                // Convert the hour to 24-hour format
                var hour24 = hour
                if (amPm == "PM" && hour != 12) {
                    hour24 += 12
                } else if (amPm == "AM" && hour == 12) {
                    hour24 = 0
                }
                // Set the calendar time
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour24)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                // Set the notification time to 1 hour before
                val time = calendar.timeInMillis - 3600000
                val intent = Intent(this, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                Toast.makeText(this, "Notification scheduled for $hour:$minute $amPm", Toast.LENGTH_SHORT).show()
                val intent2 = Intent(this, PatientHomePageActivity::class.java)
                startActivity(intent2)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Please fill in the required information completely",
                    Toast.LENGTH_LONG
                ).show()
            }


        }
    }

    private fun openLocationInGoogleMaps(locationUrl: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
            intent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback: If Google Maps is not installed, open in a browser
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
            startActivity(fallbackIntent)
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "This is my channel"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateTimeSlots(bookedAppointments: List<AppointmentData>) {
        val timeSlots = listOf(btn9am, btn10am, btn11am, btn12pm, btn2pm, btn3pm, btn4pm, btn5pm)

        timeSlots.forEach { timeSlot ->
            val hour = timeSlot.text.toString()
            if (bookedAppointments.any { it.date == Date && it.hour == hour }) {
                timeSlot.setBackgroundColor(resources.getColor(R.color.black))
                timeSlot.isEnabled = false
            } else {
                timeSlot.setBackgroundColor(resources.getColor(R.color.purple))
                timeSlot.isEnabled = true
            }
        }
    }
    fun fetchBookedAppointmentsForDate(date: String) {
        val db = FirebaseFirestore.getInstance()
        val doctorEmail = intent.getStringExtra("email")

        db.collection("doctorAppointments").document(doctorEmail!!)
            .collection("appointments")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { result ->
                val bookedAppointments = result.toObjects(AppointmentData::class.java)
                updateTimeSlots(bookedAppointments)
            }
            .addOnFailureListener { e ->
                Log.w("AppointmentActivity", "Error fetching booked appointments", e)
            }
    }

    fun addAppointmentToFirestore(
        patientEmail: String,
        doctorEmail: String,
        appointment: AppointmentData
    ) {
        val db = FirebaseFirestore.getInstance()

        val patientRef = db.collection("appointments").document(patientEmail)

        val newAppointmentRef = patientRef.collection("patientAppointments").document()

        val doctorRef = db.collection("doctorAppointments").document(doctorEmail)
        val newDoctorAppointmentRef =
            doctorRef.collection("appointments").document(newAppointmentRef.id)

        newAppointmentRef.set(appointment)
            .addOnSuccessListener {
                newDoctorAppointmentRef.set(appointment)
                    .addOnSuccessListener {
                        Log.d("AppointmentActivity", "The appointment has been added successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.w("AppointmentActivity", "An error occurred while adding a doctor's appointment", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("AppointmentActivity", "An error occurred while adding an appointment", e)
            }
    }
}

//val geoUri = "geo:0,0?q=Empire+State+Building,New+York"
//val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
//startActivity(intent)