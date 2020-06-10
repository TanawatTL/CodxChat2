package com.trap9.codxchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.trap9.codxchat.R
import kotlinx.android.synthetic.main.activity_create_ac.btn_createac
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener {
                firebaseAuth: FirebaseAuth ->
            var user = firebaseAuth.currentUser

            if(user != null) {
                var intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
            Toast.makeText(this, "Please put your email and password", Toast.LENGTH_LONG).show()
        }
        }


        btn_login.setOnClickListener {
            var email = edt_email_login.text.toString().trim()
            var password = edt_password_login.text.toString().trim()

            loginWithEmailPassword(email,password)
        }


        btn_forgotpassword.setOnClickListener{
            var intent = Intent(this,
                ForgotpasswordActivity::class.java)
            startActivity(intent)
        }

        btn_createac.setOnClickListener{
            var intent = Intent (this,
                CreateAcActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()

        mAuth!!.removeAuthStateListener(mAuthListener!!)
    }
    private fun loginWithEmailPassword(email:String,password:String){
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                        var intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("userId", mAuth!!.currentUser!!.uid)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please put your email and password", Toast.LENGTH_LONG).show()
        }
    }
}
