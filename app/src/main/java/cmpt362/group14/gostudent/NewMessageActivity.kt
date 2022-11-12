package cmpt362.group14.gostudent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {
    private lateinit var newMessage_recycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        newMessage_recycleView = findViewById(R.id.newMessage_recycleView)


        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    Log.d("newMessage",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{ item,view ->
                    val userItem = item as UserItem
                    var intent = Intent(view.context,ChatActivity::class.java)
 //                   intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                }

                newMessage_recycleView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
//        var textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_new_message)
//        textView.text = user.username
//        var imageview = viewHolder.itemView.findViewById<TextView>(R.id.imageView_new_message)
//        Picasso.get().load(user.profileImageUrl).into(imageview)

    }

    override fun getLayout(): Int {
        return R.layout.newmessage_row
    }

}

