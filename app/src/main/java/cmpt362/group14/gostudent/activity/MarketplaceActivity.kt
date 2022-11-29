package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.MarketplaceAdapter
import cmpt362.group14.gostudent.databinding.ActivityMarketplaceBinding
import cmpt362.group14.gostudent.model.Item
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMarketplaceBinding
    private var itemList = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addItemBtn.setOnClickListener() {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        db = FirebaseFirestore.getInstance()
        fetchItems()
    }

    private fun fetchItems() {
        db.collection("item")
            .orderBy("createdTime")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(HomeChatActivity.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val item: Item = dc.document.toObject(Item::class.java)
                            itemList.add(item)
                            binding.listviewItems.adapter = MarketplaceAdapter(this, itemList)
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }
}
