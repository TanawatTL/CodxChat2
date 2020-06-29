package com.trap9.codxchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trap9.codxchat.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (intent.extras != null){
            var chatId = intent!!.extras!!.get("chatid").toString()

            tv_chatid.text = chatId
        }
    }
}