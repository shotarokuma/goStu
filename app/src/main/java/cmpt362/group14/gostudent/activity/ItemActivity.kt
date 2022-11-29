package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.Item
import cmpt362.group14.gostudent.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ItemActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var seller: User? = null
    private lateinit var item: Item
    private lateinit var nameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var conditionValueTextView: TextView
    private lateinit var publicTextView: TextView
    private lateinit var doorTextView: TextView
    private lateinit var sellerNameTextView: TextView
    private lateinit var sellerImageView: ImageView
    private lateinit var itemImages: ImageView

    companion object {
        const val USER_KEY = "USER_KEY"
        const val ITEM_KEY = "ITEM_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        db = FirebaseFirestore.getInstance()
        val itemData: String? = intent.getStringExtra(ITEM_KEY)
        item = Gson().fromJson(itemData!!, Item::class.java)
        onFetchSeller(item.sellerId)

        nameTextView = findViewById(R.id.name_label)
        priceTextView = findViewById(R.id.price_label)
        descriptionTextView = findViewById(R.id.item_description)
        conditionValueTextView = findViewById(R.id.condition_value)
        publicTextView = findViewById(R.id.public_meetup)
        doorTextView = findViewById(R.id.door_pickup)
        sellerNameTextView = findViewById(R.id.seller_name)
        sellerImageView = findViewById(R.id.seller_image)
        itemImages = findViewById(R.id.itemImages)

        nameTextView.text = "Name:" + item.name
        priceTextView.text = "$" + item.price.toString()
        descriptionTextView.text = item.description
        conditionValueTextView.text = item.condition
        if (item.send == 0) {
            publicTextView.setTypeface(publicTextView.typeface, Typeface.BOLD)
        } else {
            doorTextView.setTypeface(doorTextView.typeface, Typeface.BOLD)
        }
        Picasso.get().load(item.displayImageUrl).into(itemImages)
    }

    private fun onFetchSeller(sellerId: String) {
        db.collection("user")
            .whereEqualTo("uid", item.sellerId)
            .get()
            .addOnSuccessListener {
                seller = it.documents[0].toObject(User::class.java)
                sellerNameTextView.text = seller!!.name
                Picasso.get()
                    .load(seller!!.profileImageUrl)
                    .into(sellerImageView)
            }
    }

    fun onContact(view: View) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(USER_KEY, Gson().toJson(seller))
        startActivity(intent)
    }
}
