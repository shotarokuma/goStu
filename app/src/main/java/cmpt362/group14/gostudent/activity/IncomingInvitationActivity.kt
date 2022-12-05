package cmpt362.group14.gostudent.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class IncomingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType: ImageView
    private lateinit var meetingType: String

    private lateinit var textFirstChar: TextView
    private lateinit var textUsername: TextView

    var user: User? = null
    private lateinit var imageStopInvitation:ImageView
    private lateinit var imageAcceptInvitation:ImageView

    private var toId: String? = null
    private var fromId: String? = null
    private lateinit var db: FirebaseFirestore
    private var pick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        val uid = FirebaseAuth.getInstance().uid
        db = FirebaseFirestore.getInstance()
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    inviterToken = task.result.toString()
                }
            }

        imageMeetingType = findViewById(R.id.imageMeetingType)
        meetingType = intent.getStringExtra("type").toString()

        if(meetingType != null)
        {
            if (meetingType.equals("video"))
            {
                imageMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        textFirstChar = findViewById(R.id.textFirstChar)
        textUsername = findViewById(R.id.textUsername)

        val userData: String? = intent.getStringExtra("user")
        user = Gson().fromJson(userData!!, User::class.java)
        println("--------------${user?.name}------------------")
        if(user != null)
        {
            textFirstChar.setText(user!!.name.substring(0,1))
            textUsername.setText(user!!.name)
        }

        imageStopInvitation = findViewById(R.id.imageRejectInvitation)
        imageStopInvitation.setOnClickListener{
            pick = false
            onBackPressed()
        }

        imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation)
        imageAcceptInvitation.setOnClickListener{
            pick = true
            performCallResponseMessage()
        }
    }

    private fun performCallResponseMessage() {
        fromId = FirebaseAuth.getInstance().uid
        toId = user?.uid
        val chatMessage: ChatMessage = ChatMessage(text = "", fromId = fromId!!, toId = toId!!, initiateCall = false, call = true, callResponse = pick)
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
    }


}