package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.MarketplaceAdapter
import cmpt362.group14.gostudent.databinding.ActivityMarketplaceBinding
import cmpt362.group14.gostudent.model.Item
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMarketplaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addItemBtn.setOnClickListener() {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        db = FirebaseFirestore.getInstance()
        //replace with snapshot listener?
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
                        val itemList = ArrayList<Item>()
                        it.result.documents.forEach { d ->
                            val item: Item? = d.toObject(Item::class.java)
                            if (item != null) {
                                itemList.add(item)
                            }
                        }
                        //synchro?
                        binding.listviewItems.adapter = MarketplaceAdapter(this, itemList)
                    }
                }
            )

    }

}