package cmpt362.group14.gostudent.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.ItemAdapter
import cmpt362.group14.gostudent.databinding.ActivityUserItemsBinding
import cmpt362.group14.gostudent.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class UserItemsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityUserItemsBinding
    private var itemList = ArrayList<Item>()
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()
        fetchItems()
    }

    private fun fetchItems() {
        db.collection("item")
            .orderBy("createdTime")
            .whereEqualTo("sellerId",uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(HomeChatFragment.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val item: Item = dc.document.toObject(Item::class.java)
                            itemList.add(item)
                            binding.listviewItems.adapter = ItemAdapter(this, itemList)
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }
}