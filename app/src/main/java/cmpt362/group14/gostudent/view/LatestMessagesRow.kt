package cmpt362.group14.gostudent.view

import android.widget.TextView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class LatestMessagesRow(private val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartnerUser: User? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var textViewMessage: TextView
    private lateinit var chatPartnerId: String
    override fun bind(viewHolder: ViewHolder, position: Int) {
        db = FirebaseFirestore.getInstance()
        textViewMessage = viewHolder.itemView.findViewById(R.id.latest_message_textview)
        textViewMessage.text = chatMessage.text

        chatPartnerId = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }

        db.collection("user")
            .whereEqualTo("uid", chatPartnerId)
            .get()
            .addOnCompleteListener(
                OnCompleteListener {
                    if (it.isCanceled) {
//                        TODO("Not yet implemented")
                    }

                    if (it.isSuccessful) {
                        chatPartnerUser = it.result.documents[0].toObject(User::class.java)
//                        var textV = viewHolder.itemView.findViewById<TextView>(R.id.)
//                        textV = chatPartnerUser?.name

//                        TODO("Profile")
//                    val targetIV = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_latest_message)
//                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetIV)
                    }
                }
            )
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}
