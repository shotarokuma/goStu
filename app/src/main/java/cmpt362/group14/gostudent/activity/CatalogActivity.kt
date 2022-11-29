package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class CatalogActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    companion object {
        const val ITEM_KEY = "ITEM_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        db = FirebaseFirestore.getInstance()
        fetchItems()
    }

    private fun fetchItems() {
        db.collection("item")
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
                            intent = Intent(view.context, ChatActivity::class.java)
//                            intent.putExtra(ItemActivity.ITEM_KEY, Gson().toJson())
                            startActivity(intent)
                        }
//                TODO("have to implement adapter && recycle view to show list of item dynamically")
                    }
                }
            )
    }
}
