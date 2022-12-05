package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {
    private val TAG = "CHAT ACTIVITY TAG"
    private lateinit var sendButton: Button
    private lateinit var editTextChat: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var toUser: User
    private lateinit var currentUser: User
    var callUser: User? = null
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
                Log.d(TAG, "Current user ${currentUser?.name}")
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
                            if(chatMessage.call == false)
                            {
                                Log.d(TAG, "listenForMessages: ${chatMessage.text}")
                                if (chatMessage.fromId == FirebaseAuth.getInstance().uid && chatMessage.toId == toUser.uid) {
                                    adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                                } else if (chatMessage.toId == FirebaseAuth.getInstance().uid) {
                                    adapter.add(ChatToItem(chatMessage.text, toUser))
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED ->{
                            Log.d(TAG, "listenForMessages: Message removed")
                        }
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
                editTextChat.text.clear()
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        db.collection("latest-message")
            .document()
            .set(chatMessage)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.call, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_video_call -> {
            /*
            Need to add the functionality to video call
             */

            val intent = Intent(this, OutgoingInvitationActivity::class.java)
//            val userItem = item as UserItem
            intent.putExtra("user", Gson().toJson(toUser))
            println("outgoing user: -------    ${toUser.name}")
            intent.putExtra("type","video")
            startActivity(intent)
            performSendCallMessage()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    /*
    Call Flow:
    1. Send message with call = true, initiateCall = true, callRespones = false, open outgoingActivity
    2. When receiving above ^ message, open IncomingActivity
    2. Incoming activity sends either pick = true or pick = false, with callResponse = true
     */
    private fun performSendCallMessage() {
//        val chat = editTextChat.text.toString()
        fromId = FirebaseAuth.getInstance().uid
        val toUserData: String? = intent.getStringExtra(NewMessageActivity.USER_KEY)
        user = Gson().fromJson(toUserData!!, User::class.java)
        toId = user.uid
        val chatMessage: ChatMessage = ChatMessage(text = "", fromId = fromId!!, toId = toId!!, initiateCall = true, call = true, callResponse = false)
        db.collection("user-message")
            .document()
            .set(chatMessage)
            .addOnSuccessListener {
//                editTextChat.text.clear()
//                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        db.collection("latest-message")
            .document()
            .set(chatMessage)
        // adapter.clear()
        listenForCallMessages()
    }

    private fun listenForCallMessages() {
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
//                            Log.d(TAG, "listenForMessages: ${chatMessage.text}")
                            if (chatMessage.initiateCall == false)
                            {
                                if(chatMessage.call == true && chatMessage.callResponse == true)
                                {
                                    Toast.makeText(this, "ACCEPTED", Toast.LENGTH_SHORT).show()
                                    //Delete call messages when accepted
                                    db.collection("user-message")
                                        .whereIn("fromId", listOf(fromId, toId))
                                        .whereEqualTo("call", true).get().addOnSuccessListener {
                                            it.documents.forEach() {
                                                it.reference.delete().addOnCompleteListener {
                                                    println("delete message")
                                                }
                                            }
                                        }
                                }
                                else if(chatMessage.call == true && chatMessage.callResponse == false)
                                {
                                    Toast.makeText(this, "REJECTED", Toast.LENGTH_SHORT).show()
                                    //delete calls when rejected
                                    db.collection("user-message")
                                        .whereIn("fromId", listOf(fromId,toId))
                                        .whereEqualTo("call", true).get().addOnSuccessListener {
                                            it.documents.forEach() {
                                                it.reference.delete().addOnCompleteListener {
                                                    println("delete message")
                                                }
                                            }
                                        }
                                    finish()
                                }
                            }
                            else
                            {
                                if (chatMessage.fromId == FirebaseAuth.getInstance().uid && chatMessage.toId == toUser.uid)
                                {

                                }
                                else if (chatMessage.toId == FirebaseAuth.getInstance().uid) {
                                    val intent = Intent(this, IncomingInvitationActivity::class.java)
                                    db.collection("user")
                                        .whereEqualTo("uid", toId)
                                        .get()
                                        .addOnSuccessListener {
                                            callUser = it.documents[0].toObject(User::class.java)
                                            println("$---caller--------${callUser?.name}---------------")
                                        }
                                    intent.putExtra("user", Gson().toJson(callUser))
                                    intent.putExtra("type","video")
                                    startActivity(intent)

                                }
                            }

                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED ->{
                            Log.d(TAG, "listenForCallMessages: Message Removed")
                        }
                    }
                }
            }
    }

}
