package com.example.doctorsappointment

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DoctorCustomAdapter (private val context: Activity, private val list:List<DoctorData>) : ArrayAdapter<DoctorData>(context,
    R.layout.custom_doctor_list, list)
{
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = context.layoutInflater.inflate(R.layout.custom_doctor_list,null,true)
        val r_name = rootView.findViewById<TextView>(R.id.r_name)
        val r_age = rootView.findViewById<TextView>(R.id.r_age)
        val r_field = rootView.findViewById<TextView>(R.id.r_field)
        val r_image = rootView.findViewById<ImageView>(R.id.r_img)
        val r_hospitalname = rootView.findViewById<TextView>(R.id.r_hospitalname)
        val r_hospitallocation = rootView.findViewById<TextView>(R.id.r_hospitallocaion)
        val r_hospitalurl = rootView.findViewById<TextView>(R.id.r_hospitalurl)

        val user = list[position]
        r_name.text = "${user.first} ${user.last}"
        r_age.text = "Age: " + user.age.toString()
        r_hospitalname.text = "HOSPITAL NAME : "+ user.hospitalname
        r_hospitallocation.text = "HOSPITAL LOCATION : " + user.hospitaladdress
        r_field.text = user.field
        r_hospitalurl.text = user.hospitalurl
        Glide.with(context).load(user.image).into(r_image)


        return rootView
    }

}