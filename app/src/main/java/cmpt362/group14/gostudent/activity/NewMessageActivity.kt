package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var newMessageRecycleView: RecyclerView

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        newMessageRecycleView = findViewById(R.id.newMessage_recycleView)
        db = FirebaseFirestore.getInstance()
        fetchUsers()
    }

    private fun fetchUsers() {
        db.collection("user")
            .get()
            .addOnCompleteListener(
                OnCompleteListener {
                    if (it.isCanceled) {
                        TODO("Not yet implemented")
                    }

                    if (it.isSuccessful) {
                        val adapter: GroupAdapter<ViewHolder> = GroupAdapter<ViewHolder>()
                        it.result.documents.forEach { d ->
                            val user: User? = d.toObject(User::class.java)
                            if (user != null) {
                                adapter.add(UserItem(user))
                            }
                        }
                        adapter.setOnItemClickListener { item, view ->
                            val userItem = item as UserItem
                            intent = Intent(view.context, ChatActivity::class.java)
                            intent.putExtra(USER_KEY, Gson().toJson(userItem.user))
                            startActivity(intent)
                        }
                        newMessageRecycleView.adapter = adapter
                    }
                }
            )
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val textView: TextView = viewHolder.itemView.findViewById<TextView>(R.id.textView_new_message)
        textView.text = user.name

        val imageview: ImageView = viewHolder.itemView.findViewById(R.id.imageView_new_message)
        Picasso.get().load(user.profileImageUrl).into(imageview)
    }

    override fun getLayout(): Int {
        return R.layout.newmessage_row
    }
}
