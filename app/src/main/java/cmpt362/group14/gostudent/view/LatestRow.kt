package cmpt362.group14.gostudent.view

import android.widget.ImageView
import android.widget.TextView
import cmpt362.group14.gostudent.ChatMessage
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class LatestMessagesRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textViewMessage = viewHolder.itemView.findViewById<TextView>(R.id.latest_message_textview)
        textViewMessage.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                TODO("Not yet implemented")
                chatPartnerUser = p0.getValue(User::class.java)
                var textV = viewHolder.itemView.findViewById<TextView>(R.id.latest_message_textview).text
                textV = chatPartnerUser?.username

                val targetIV = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_latest_message)
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetIV)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}