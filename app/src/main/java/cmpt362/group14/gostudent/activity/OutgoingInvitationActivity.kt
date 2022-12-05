package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.JsonToken
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User

import com.google.android.gms.common.api.Response
import com.google.android.gms.common.api.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.prefs.Preferences

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType:ImageView
    private lateinit var meetingType: String
    private lateinit var textFirstChar: TextView
    private lateinit var textUsername: TextView
    private lateinit var currentUser: User
    private var user: User? = null
    private lateinit var imageStopInvitation:ImageView


    private var toId: String? = null
    private var fromId: String? = null
    private lateinit var db: FirebaseFirestore

    private lateinit var preferenceManager: SharedPreferences
    private var inviterToken: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_invitation)

        val uid = FirebaseAuth.getInstance().uid
        preferenceManager = getDefaultSharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    inviterToken = task.result.toString()
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

        if(user != null)
        {
            textFirstChar.setText(user!!.name.substring(0,1))
            textUsername.setText(user!!.name)
        }

//        listenForCallResponseMessages()

        imageStopInvitation = findViewById(R.id.imageStopInvitation)
        imageStopInvitation.setOnClickListener{
            finish()
        }

//        if (meetingType != null && user != null)
//        {
//            initiateMeeting(meetingType)
//        }

    }


}