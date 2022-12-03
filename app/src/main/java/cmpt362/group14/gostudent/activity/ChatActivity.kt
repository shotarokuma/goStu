package cmpt362.group14.gostudent.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import  android.widget.EditText
import  android.widget.Toast
import  android.widget.ImageView
import  android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import cmpt362.group14.gostudent.service.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class ChatActivity : AppCompatActivity() {
    private val TAG = "CHAT ACTIVITY TAG"
    private lateinit var sendButton: Button
    private lateinit var editTextChat: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var toUser: User
    private lateinit var currentUser: User
    private var toId: String? = null
    private var fromId: String? = null
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userData: String? = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = Gson().fromJson(userData!!, User::class.java)
        Log.d(TAG, "onCreate: toUser: ${toUser.name}")
        supportActionBar?.title = toUser.name

        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerview_chat)
        recyclerView = findViewById(R.id.recyclerview_chat)
        recyclerView.adapter = adapter

//        listenForMessages()
        getCurrentUser()

        sendButton = findViewById(R.id.send_chat_button)
        editTextChat = findViewById(R.id.edittext_chat)
        sendButton.setOnClickListener {
            performSendMessage()
        }
    }

    /**
     * Sets currentUser, then launches listenForMessages()
     */
    private fun getCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener {
                currentUser = it.documents[0].toObject(User::class.java)!!
                Log.d(TAG, "Current user ${currentUser.name}")
                listenForMessages()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun listenForMessages() {
        fromId = FirebaseAuth.getInstance().uid
        toId = toUser.uid
        db.collection("user-message")
            .whereIn("fromId", listOf(fromId, toId))
            .orderBy("createdTime")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage: ChatMessage =
                                dc.document.toObject(ChatMessage::class.java)
                            Log.d(TAG, "listenForMessages: ${chatMessage.text}")
                            if (chatMessage.fromId == FirebaseAuth.getInstance().uid && chatMessage.toId == toUser.uid) {
                                adapter.add(ChatFromItem(chatMessage.text, currentUser))
                            } else if (chatMessage.toId == FirebaseAuth.getInstance().uid) {
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
        val toUserData: String? = intent.getStringExtra(NewMessageActivity.USER_KEY)
        user = Gson().fromJson(toUserData!!, User::class.java)
        toId = user.uid
        val chatMessage: ChatMessage = ChatMessage(text = chat, fromId = fromId!!, toId = toId!!)
        db.collection("user-message")
            .document()
            .set(chatMessage)
            .addOnSuccessListener {
                onSendToServer(chat)
                editTextChat.text.clear()
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        db.collection("latest-message")
            .document()
            .set(chatMessage)
        // adapter.clear()
        // listenForMessages()
    }

    private fun onSendToServer(chat: String) {
        val service = Retrofit.Builder()
            .baseUrl("https://p2fe2plvrbbi7f6lnjypb4qfh40yxnpw.lambda-url.us-east-2.on.aws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        db.collection("user")
            .whereEqualTo("uid", toId)
            .get()
            .addOnSuccessListener {
                val token = it.documents[0].toObject(User::class.java)!!.fcm
                thread {
                    try {
                        service.sendMessage(
                            token = token,
                            body = chat,
                            title = resources.getString(R.string.new_message)
                        ).execute()
                    } catch (e: HttpException) {
                        Toast.makeText(this, "Sending message failed.", Toast.LENGTH_SHORT).show()
                        Log.e("text", e.localizedMessage)
                    }
                }
            }
    }

    class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val textView: TextView = viewHolder.itemView.findViewById(R.id.textView_from)
            textView.text = text

            val targetImageView: ImageView = viewHolder.itemView.findViewById(R.id.imageView_from_row)
            Picasso.get().load(user.profileImageUrl).into(targetImageView)
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

            val targetImageView: ImageView = viewHolder.itemView.findViewById(R.id.imageView_to_row)
            Picasso.get().load(user.profileImageUrl).into(targetImageView)
        }
    }
}
