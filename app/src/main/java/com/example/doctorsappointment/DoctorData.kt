package com.example.doctorsappointment

data class DoctorData(var UID: String? = null,
                      var first: String? = null,
                      var last: String? = null,
                      var age: String? = null,
                      var field : String? = null,
                      var email: String? = null ,
                      var password: String? = null,
                      var image: String? = "data:image/jpeg;base64,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",
                      var hospitalname:String? =null,
                      var hospitaladdress:String? =null,
                      var hospitalurl:String? =null,
                      var address: String? = null,
                      var distance: Float = 0f)
