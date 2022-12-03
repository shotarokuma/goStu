package cmpt362.group14.gostudent.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.Item
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

class EditItemActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var seller: User? = null
    private lateinit var item: Item


    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var conditionSpinner: Spinner
    private lateinit var publicCheckBox: CheckBox
    private lateinit var meetUpCheckBox: CheckBox
    var array = arrayOf("New", "Used", "Fair")

    companion object {
        const val USER_KEY = "USER_KEY"
        const val ITEM_KEY = "ITEM_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        db = FirebaseFirestore.getInstance()
        val itemData: String? = intent.getStringExtra(ITEM_KEY)
        item = Gson().fromJson(itemData!!, Item::class.java)
        onFetchSeller(item.sellerId)


        nameEditText = findViewById(R.id.name_input)
        priceEditText = findViewById(R.id.price_input)
        descriptionEditText = findViewById(R.id.item_description)
        conditionSpinner = findViewById(R.id.condition_spinner)
        publicCheckBox = findViewById(R.id.public_meetup)
        meetUpCheckBox = findViewById(R.id.door_pickup)

        nameEditText.setText(item.name)
        priceEditText.setText(item.price.toString())
        descriptionEditText.setText(item.description)

        /*
        need to set stored spinner value
         */
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conditionSpinner.adapter = adapter
        val spinnerPosition: Int = adapter.getPosition(item.condition)
        conditionSpinner.setSelection(spinnerPosition)

        if (item.send == 0) {
            publicCheckBox.isChecked = true
        } else {
            meetUpCheckBox.isChecked = true
        }
    }

    private fun onFetchSeller(sellerId: String) {
        db.collection("user")
            .whereEqualTo("uid", item.sellerId)
            .get()
            .addOnSuccessListener {
                seller = it.documents[0].toObject(User::class.java)
            }
    }

    fun onContact(view: View) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(USER_KEY, Gson().toJson(seller))
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            /*
            Need to add the functionality to delete the item
             */
            finish()
            System.out.close()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    fun onStoreItem(view: View) {

    }

    fun onAddItemImg(view: View) {


    }
}
