package cmpt362.group14.gostudent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.view.LatestMessagesRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class HomeChat : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_chat)

        var latest_recyclerView = findViewById<RecyclerView>(R.id.latest_message_recyclerView)
        latest_recyclerView.adapter = adapter
        latest_recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatActivity::class.java)


            val row = item as LatestMessagesRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenLatestMessages()

        fetchUser()

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null)
        {
            var intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }


    private fun refreshRecyclerViewmessages() {
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessagesRow(it))
        }

    }



    var latestMessagesMap = HashMap<String, ChatMessage>()

    private fun listenLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewmessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewmessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    val adapter = GroupAdapter<ViewHolder>()


    private fun fetchUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("HomeChat", "Current user ${currentUser?.username}")
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId)
        {
            R.id.new_msg -> {
                var intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)

        }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}