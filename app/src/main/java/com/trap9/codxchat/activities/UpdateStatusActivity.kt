package com.trap9.codxchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.trap9.codxchat.R
import kotlinx.android.synthetic.main.activity_status.*

class UpdateStatusActivity : AppCompatActivity() {

    var mDatabase: FirebaseDatabase? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

//        if (intent.extras != null) {
//            var status = intent.extras!!.get("status").toString()
//            edt_status.setText(status)
//        }

        btn_update_status.setOnClickListener {
            var updateStatus = edt_status.text.toString().trim()

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var uid = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance()
            var statusRef = mDatabase!!.reference.child("Users").child(uid).child("status")
            statusRef.setValue(updateStatus).addOnCompleteListener {
                    task: Task<Void> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Update status successful", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Can't update status", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

