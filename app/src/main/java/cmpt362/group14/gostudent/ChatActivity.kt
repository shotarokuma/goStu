package cmpt362.group14.gostudent

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {
    private lateinit var send_button: Button
    private lateinit var editText_chat: EditText
    val adapter = GroupAdapter<ViewHolder>()
    private lateinit var recyclerView: RecyclerView

    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.uid

        recyclerView = findViewById(R.id.recyclerview_chat)
        var recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chat)
        recyclerView.adapter = adapter
//        val adapter = GroupAdapter<ViewHolder>()

//        adapter.add(ChatFromItem("message from"))
//        adapter.add(ChatToItem("message to"))

        listenForMessages()



        send_button = findViewById(R.id.send_chat_button)
        editText_chat = findViewById(R.id.edittext_chat)
        send_button.setOnClickListener {
            performSendMessage()
        }
    }
    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var chat_message = snapshot.getValue(ChatMessage::class.java)
//                println(chat_message)
                if (chat_message != null) {

                    if(chat_message.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = HomeChat.currentUser
                        adapter.add(ChatFromItem(chat_message.text, currentUser!!))
                    }
                    else
                    {

                        adapter.add(ChatToItem(chat_message.text, toUser!!))
                    }
                }
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })

    }


    private fun performSendMessage() {
        val chat = editText_chat.text.toString()

        var fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        var toId = user?.uid

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val chatMessage = ChatMessage(reference.key!!, chat, fromId!!, toId!!, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage).addOnSuccessListener {
            println("Saved message: ${reference.key}")
            editText_chat.text.clear()
            recyclerView.scrollToPosition(adapter.itemCount-1)
        }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}

class ChatFromItem(var text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_from)
        textView.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(var text: String, val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_to)
        textView.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(uri).into(targetImageView)
    }

}