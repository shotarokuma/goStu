package cmpt362.group14.gostudent

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var editTextChat: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var toUser: User
    private var toId: String? = null
    private var fromId: String? = null
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userData: String? = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = Gson().fromJson(userData!!, User::class.java)
        supportActionBar?.title = toUser.uid

        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerview_chat)
        recyclerView = findViewById(R.id.recyclerview_chat)
        recyclerView.adapter = adapter

        listenForMessages()

        sendButton = findViewById(R.id.send_chat_button)
        editTextChat = findViewById(R.id.edittext_chat)
        sendButton.setOnClickListener {
            performSendMessage()
        }
    }
    private fun listenForMessages() {
        fromId = FirebaseAuth.getInstance().uid
        toId = toUser.uid
        db.collection("user-message")
            .whereEqualTo("fromId", fromId)
            .whereEqualTo("toId", toId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(HomeChat.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage: ChatMessage = value.documents[0] as ChatMessage
                            if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                                val currentUser: User? = HomeChat.currentUser
                                adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                            } else {
                                adapter.add(ChatToItem(chatMessage.text, toUser))
                            }
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }

    private fun performSendMessage() {
        val chat = editTextChat.text.toString()

        fromId = FirebaseAuth.getInstance().uid
        user = intent.getSerializableExtra(NewMessageActivity.USER_KEY) as User
        toId = user.uid
        val chatMessage: ChatMessage = ChatMessage(text = chat, fromId = fromId!!, toId = toId!!)
        db.collection("user-message")
            .document()
            .set(chat)
            .addOnSuccessListener {
                editTextChat.text.clear()
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        db.collection("latest-message")
            .document()
            .set(chat)
    }

    class ChatFromItem(var text: String, val user: User) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val textView: TextView = viewHolder.itemView.findViewById(R.id.textView_from)
            textView.text = text

            TODO("profile image")
//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)
        }

        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }
    }

    class ChatToItem(var text: String, val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chat_to_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val textView: TextView = viewHolder.itemView.findViewById(R.id.textView_to)
            textView.text = text

            TODO("profile image")
//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)
//        Picasso.get().load(uri).into(targetImageView)
        }
    }
}
