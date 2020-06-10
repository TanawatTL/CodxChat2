package com.trap9.codxchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.trap9.codxchat.R
import kotlinx.android.synthetic.main.activity_update_displayname.*

class UpdateDisplaynameActivity : AppCompatActivity() {

    var mDatabase: FirebaseDatabase? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_displayname)

//        if (intent.extras != null) {
//            var displayname = intent.extras!!.get("display_name").toString()
//            edt_display_name_profile.setText(displayname)
//        }
        btn_update_display_name.setOnClickListener {
            var updateDisplay_name = edt_display_name_profile.text.toString().trim()

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var uid = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance()
            var display_nameRef = mDatabase!!.reference.child("Users").child(uid).child("display_name")
                display_nameRef.setValue(updateDisplay_name).addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Update display name successful", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this,"Can't update display name", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}
