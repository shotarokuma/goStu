package cmpt362.group14.gostudent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {
    private lateinit var send_button: Button
    private lateinit var editText_chat: EditText
    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.uid


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
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var chat_message = snapshot.getValue(ChatMessage::class.java)
//                println(chat_message)
                if (chat_message != null) {

                    if(chat_message.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chat_message.text))
                    }
                    else
                    {
                        adapter.add(ChatToItem(chat_message.text))
                    }
                }
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


    private fun performSendMessage()
    {
        val chat = editText_chat.text.toString()

        var fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        var toId = user?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(reference.key!!, chat, fromId!!, toId!!, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage).addOnSuccessListener { println("Saved message: ${reference.key}") }
    }
}

class ChatFromItem(var text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_from)
        textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(var text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_to)
        textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}