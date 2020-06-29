package com.trap9.codxchat.flagments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.trap9.codxchat.R
import com.trap9.codxchat.activities.ProfileActivity
import com.trap9.codxchat.models.User
import com.trap9.codxchat.models.UserHolder
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {

    var mDatabase : FirebaseDatabase? =null
    var mAuth : FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        var linearLayoutManger = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        var query = mDatabase!!.reference.child("Users").orderByChild("display_name")

        var option = FirebaseRecyclerOptions.Builder<User>().setQuery(query,User::class.java).setLifecycleOwner(this).build()

        var adapter = object : FirebaseRecyclerAdapter<User,UserHolder>(option){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
                return UserHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_row,parent,false))
            }

            override fun onBindViewHolder(holder: UserHolder, position: Int, model: User) {
                holder.bind(model)

                var friendId = getRef(position).key.toString()
                var userId = mAuth!!.currentUser!!.uid
                var chatId:String? = null

                var chatRef = mDatabase!!.reference.child("Chat").child(userId).child(friendId).child("chat_id")

                holder.itemView.setOnClickListener {
                    var options = arrayOf("Open profile","Send Massage")
                    var builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Select opion")
                    builder.setItems(options){DialogInterface, i ->
                        if (i==0){
                            var intent = Intent(context,ProfileActivity::class.java)
                                intent.putExtra("userid",friendId)
                            startActivity(intent)
                        }else{

                            chatRef.addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        chatId = dataSnapshot.value.toString()
                                    }else{
                                        var massageRef = mDatabase!!.reference.child("Massages").push()

                                        var userList = HashMap<String,String>()
                                        userList.put("0",userId)
                                        userList.put("1",friendId)

                                        massageRef.child("user_list").setValue(userList)

                                        chatId = massageRef.key.toString()

                                        var userDataRef = mDatabase!!.reference.child("Chat").child(userId).child(friendId).child("chat_id")
                                        userDataRef.setValue(chatId)

                                        var frindDataRef = mDatabase!!.reference.child("Chat").child(friendId).child(userId).child("chat_id")
                                        frindDataRef.setValue(chatId)
                                    }

                                    var intent = Intent(context,ProfileActivity::class.java)
                                    intent.putExtra("chatid",chatId)
                                    intent.putExtra("friendid",friendId)
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                    builder.show()
                }
            }

        }
        recycler_users.setHasFixedSize(true)
        recycler_users.layoutManager = linearLayoutManger
        recycler_users.adapter = adapter
    }

}
