package com.trap9.codxchat.models

import android.view.Display

class User() {
    var display_name:String? = null
    var status:String? = null
    var image:String? = null
    var thumb_image:String? = null

    constructor(display_name:String,status:String,image:String,thumb_image:String):this(){
        this.display_name = display_name
        this.status = status
        this.image = image
        this.thumb_image = thumb_image
    }

}