package com.trap9.codxchat.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.util.IOUtils.toByteArray
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.trap9.codxchat.R
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_your_profile.*
import java.io.ByteArrayOutputStream
import java.io.File

class YourProfileActivity : AppCompatActivity() {

    var mAuth:FirebaseAuth? = null
    var mDatabase:FirebaseDatabase? = null
    var mStorage: FirebaseStorage? = null
    var GALLERY_ID:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_profile)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()

        var userId = mAuth!!.currentUser!!.uid

        var userRef = mDatabase!!.reference.child("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                finish()
            }

            override fun onDataChange(dataSnapshot : DataSnapshot) {
                var display_name = dataSnapshot.child("display_name").value.toString()
                var status = dataSnapshot.child("status").value.toString()
                var image = dataSnapshot.child("image").value.toString()

                tv_display_name_profile.text = display_name
                tv_status_profile.text = status

                if (image != "default") {
                    Picasso.get().load(image).placeholder(R.drawable.ic_person).into(iv_user_image)
                }
            }
        })
        btn_change_username.setOnClickListener{
            var intent = Intent(this,UpdateDisplaynameActivity::class.java)
            intent.putExtra("Display_name",tv_display_name_profile.text.toString())
            startActivity(intent)
        }

        btn_update_status.setOnClickListener{
            var intent = Intent(this,UpdateStatusActivity::class.java)
            intent.putExtra("Display_name",tv_status_profile.text.toString())
            startActivity(intent)}

        btn_change_picture.setOnClickListener{
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_ID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_ID) {
            var image = data!!.data
            CropImage.activity(image).setAspectRatio(1,1).start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            var result = CropImage.getActivityResult(data)

            var resultUni = result.uri
            var thumbFile = File(resultUni.path)

            var thumbBitmap = Compressor (this)
                .setMaxHeight(200)
                .setMaxWidth(200)
                .setQuality(65)
                .compressToBitmap(thumbFile)

            var byteArray = ByteArrayOutputStream()
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArray)
            var thumbByteArray = byteArray.toByteArray()
            var userId = mAuth!!.currentUser!!.uid

            var imageRef = mStorage!!.reference.child("profile_image").child("$userId.jpg")
            var thumbRef = mStorage!!.reference.child("profile_image").child("thump_image").child("$userId.jpg")

            imageRef.putFile(resultUni).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }

                return@Continuation imageRef.downloadUrl
            }).addOnCompleteListener {
                task: Task<Uri> ->
                if (task.isComplete){
                    var imageUri = task.result.toString()
                    var uploadTask: UploadTask = thumbRef.putBytes(thumbByteArray)

                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                            task ->
                        if (!task.isSuccessful){
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation thumbRef.downloadUrl})
                        .addOnCompleteListener { task: Task<Uri> ->
                            if (task.isComplete){
                                var thumbUri = task.result.toString()
                                var updateObject =HashMap<String,Any>()
                                updateObject.put("image",imageUri)
                                updateObject.put("thumb_image",thumbUri)

                                mDatabase!!.reference.child("Users").child(userId)
                                    .updateChildren(updateObject)
                                    .addOnCompleteListener {
                                        if (task.isSuccessful){
                                            Toast.makeText(this,"Upload Sucessful",Toast.LENGTH_LONG)
                                                .show()
                                        }else{
                                            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        }
                }
            }

        }
    }

}
