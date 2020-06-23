package com.trap9.codxchat.models

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trap9.codxchat.R
import kotlinx.android.synthetic.main.user_row.view.*

class UserHolder(val customView: View): RecyclerView.ViewHolder(customView) {
        fun bind(user: User){
            customView.tv_name_row?.text = user.display_name
            customView.tv_status_row?.text = user.status
            if (!user.thumb_image!!.equals("default")){
                Picasso.get().load(user.thumb_image).placeholder(R.drawable.ic_person).into(customView.iv_user_row)
            }
        }
}