package cmpt362.group14.gostudent.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddItemActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var newItem: Item
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var conditionSpinner: Spinner
    private lateinit var publicCheckBox: CheckBox
    private lateinit var meetUpCheckBox: CheckBox
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private var send: Int = 0
    private var galleryImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        nameEditText = findViewById(R.id.name_input)
        priceEditText = findViewById(R.id.price_input)
        descriptionEditText = findViewById(R.id.item_description)
        conditionSpinner = findViewById(R.id.condition_spinner)
        publicCheckBox = findViewById(R.id.public_meetup)
        meetUpCheckBox = findViewById(R.id.door_pickup)
        publicCheckBox.isChecked = true


        priceEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        publicCheckBox.setOnClickListener {
            send = 0
            if (meetUpCheckBox.isChecked) {
                meetUpCheckBox.isChecked = false
            }
        }

        meetUpCheckBox.setOnClickListener {
            send = 1
            if (publicCheckBox.isChecked) {
                publicCheckBox.isChecked = false
            }
        }

        galleryResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                galleryImgUri = it.data?.data

//                TODO("have to image button to show selected img")
//                profileImageButton.setImageURI(galleryImgUri)
            }
        }
    }

    fun onStoreItem(view: View) {
        val fname = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$fname")
        val seller = FirebaseAuth.getInstance().currentUser

        val putFile = ref.putFile(galleryImgUri!!)
        putFile.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                newItem = Item(
                    name = nameEditText.text.toString(),
                    price = priceEditText.text.toString().toDouble(),
                    sellerId = seller!!.uid,
                    description = descriptionEditText.text.toString(),
                    condition = conditionSpinner.selectedItem.toString(),
                    send = send,
                    displayImageUrl = it.toString(),
                )

                db.collection("item")
                    .document()
                    .set(newItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sell your ${nameEditText.text} Successful", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
        }
    }

    fun onAddItemImg(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResult.launch(intent)
    }
}
