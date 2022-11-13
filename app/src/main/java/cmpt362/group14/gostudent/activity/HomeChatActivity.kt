package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import cmpt362.group14.gostudent.view.LatestMessagesRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class HomeChatActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private lateinit var latestView: RecyclerView
    private var adapter = GroupAdapter<ViewHolder>()

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_chat)

        latestView = findViewById(R.id.latest_message_recyclerView)
        latestView.adapter = adapter
        latestView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().uid.toString()

        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, "123")
            val intent: Intent = Intent(this, ChatActivity::class.java)

            val row = item as LatestMessagesRow
            intent.putExtra(NewMessageActivity.USER_KEY, Gson().toJson(row.chatPartnerUser))
            startActivity(intent)
        }

        listenLatestMessages()

        fetchUser()

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun refreshRecyclerViewmessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessagesRow(it))
        }
    }

    var latestMessagesMap = HashMap<String, ChatMessage>()

    private fun listenLatestMessages() {
        val fromId: String? = FirebaseAuth.getInstance().uid
        db.collection("user")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage = dc.document.data
                            println(chatMessage)
                            refreshRecyclerViewmessages()
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }

    private fun fetchUser() {
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener {
                currentUser = it.documents[0].toObject(User::class.java)
                Log.d("HomeChat", "Current user ${currentUser?.name}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_msg -> {
                intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                intent = Intent(this, SignUpActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
