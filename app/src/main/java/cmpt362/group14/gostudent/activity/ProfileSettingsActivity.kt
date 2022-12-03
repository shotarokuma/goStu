package cmpt362.group14.gostudent.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private var galleryImgUri: Uri? = null
    private lateinit var user: User
    private lateinit var storage: FirebaseStorage
    private val TAG = "ProfileUpdate"
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profileImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        userNameEditText = findViewById(R.id.editTextUserName)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        saveButton = findViewById(R.id.save_changes)
        cancelButton = findViewById(R.id.cancel_changes)
        profileImageButton = findViewById(R.id.profileImageButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()

        fetchUserData()

        saveButton.setOnClickListener {
            val name: String = userNameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            updateAccount(name, email, password)
        }

        cancelButton.setOnClickListener {
            finish()
        }

    }

    private fun updateAccount(name: String, email: String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Please enter an email or password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (galleryImgUri == null) {
            Toast.makeText(
                this,
                "Please enter a profile picture",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        storeAccount(user.uid, name, email, password)
    }

    private fun storeAccount(uid: String, name: String, email: String, password: String) {
        // store image in firebase storage
        val fname = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$fname")

        val putFile = ref.putFile(galleryImgUri!!)
        putFile.addOnSuccessListener {
            Log.d(TAG, "storeImage: success")
            // download url, then make new user
//            ref.downloadUrl.addOnSuccessListener {
//                newUser = User(
//                    uid = uid,
//                    name = name,
//                    mail = email,
//                    password = password,
//                    profileImageUrl = it.toString()
//                )
//                db.collection("user")
//                    .document()
//                    .set(newUser)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Create Account Successful", Toast.LENGTH_SHORT).show()
//                        val user: FirebaseUser? = auth.currentUser
//                        updateUI(user)
//                    }
//            }
        }
        putFile.addOnFailureListener {
            Toast.makeText(this, "Store Image Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchUserData(){
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener {
                user = it.documents[0].toObject(User::class.java)!!
//                println("${user?.name}")
                if(user != null)
                {
                    userNameEditText.setText(user.name)
                    emailEditText.setText(user.mail)
                    passwordEditText.setText(user.password)
                    /*
                    Unable to display the stored image
                     */
                    profileImageButton.setImageURI(user.profileImageUrl.toUri())
                }

            }
            .addOnFailureListener { exception ->
                Log.w(HomeChatFragment.TAG, "Error getting documents: ", exception)
            }
    }


}