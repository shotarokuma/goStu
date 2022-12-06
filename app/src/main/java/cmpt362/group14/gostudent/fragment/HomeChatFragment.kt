package cmpt362.group14.gostudent.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.activity.ChatActivity
import cmpt362.group14.gostudent.activity.NewMessageActivity
import cmpt362.group14.gostudent.activity.SignUpActivity
import cmpt362.group14.gostudent.model.ChatMessage
import cmpt362.group14.gostudent.model.User
import cmpt362.group14.gostudent.view.LatestMessagesRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*
import kotlin.collections.HashMap

class HomeChatFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private lateinit var latestView: RecyclerView
    private var adapter = GroupAdapter<ViewHolder>()
    private var latestMessagesList = HashMap<String, ChatMessage>()

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_home_chat, null)
        super.onCreate(savedInstanceState)

        latestView = view.findViewById(R.id.latest_message_recyclerView)
        latestView.adapter = adapter
        latestView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().uid.toString()

        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, "123")
            val intent: Intent = Intent(requireContext(), ChatActivity::class.java)

            val row = item as LatestMessagesRow
            intent.putExtra(NewMessageActivity.USER_KEY, Gson().toJson(row.chatPartnerUser))
            startActivity(intent)
        }

        listenLatestMessages()

        fetchUser()

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return view
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        val result = latestMessagesList.toList().sortedBy { (_, value) -> value.createdTime }.asReversed().toMap()
        result.forEach {
            adapter.add(LatestMessagesRow(it.value))
        }
    }

    private fun listenLatestMessages() {
        val toId: String? = FirebaseAuth.getInstance().uid
        db.collection("latest-message")
            .whereEqualTo("toId", toId)
            .orderBy("createdTime")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    val chatMessage = dc.document.toObject(ChatMessage::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "listenLatestMessages: ${chatMessage.text}")
                            if (latestMessagesList.containsKey(chatMessage.fromId)) {
                                var time = latestMessagesList.get(chatMessage.fromId)?.createdTime?.time
                                if (time != null) {
                                    if ((time - chatMessage.createdTime.time) < 0) {
                                        latestMessagesList.put(chatMessage.fromId, chatMessage)
                                    }
                                }
                            } else {
                                latestMessagesList.put(chatMessage.fromId, chatMessage)
                            }
//                            latestMessagesList.add(0, chatMessage)
                            refreshRecyclerViewMessages()
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
    // TODO create options menu in new Activity
    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_msg -> {
                //??
                val intent = Intent(requireContext(), NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out -> {
                //??
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), SignUpActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

     */
}
