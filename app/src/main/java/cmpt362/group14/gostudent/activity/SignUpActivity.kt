package cmpt362.group14.gostudent.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private val TAG = "SignUpActivity"
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var profileImageButton: ImageButton
    private var galleryImgUri: Uri? = null
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var newUser: User
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userNameEditText = findViewById(R.id.editTextUserName)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.loginButton)
        loginTextView = findViewById(R.id.loginTextView)
        profileImageButton = findViewById(R.id.profileImageButton)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        galleryResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                galleryImgUri = it.data?.data
                profileImageButton.setImageURI(galleryImgUri)
            }
        }

        registerButton.setOnClickListener {
            val name: String = userNameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            createAccount(name, email, password)
        }
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        profileImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            galleryResult.launch(intent)
        }
    }

    private fun createAccount(name: String, email: String, password: String) {

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
        val result = auth.createUserWithEmailAndPassword(email, password)
        result.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // update UI with account details
                Log.d(TAG, "CreateUser success, uid: ${task.result.user!!.uid}")
                storeAccount(task.result.user!!.uid, name, email, password)
            } else {
                // tell user that authentication failed, update UI
                Log.w(TAG, "CreateUser fail", task.exception)
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }
    private fun storeAccount(uid: String, name: String, email: String, password: String) {
        // store image in firebase storage
        val fname = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$fname")

        val putFile = ref.putFile(galleryImgUri!!)
        putFile.addOnSuccessListener {
            Log.d(TAG, "storeImage: success")
            // download url, then make new user
            ref.downloadUrl.addOnSuccessListener {
                newUser = User(
                    uid = uid,
                    name = name,
                    mail = email,
                    password = password,
                    profileImageUrl = it.toString()
                )
                db.collection("user")
                    .document()
                    .set(newUser)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Create Account Successful", Toast.LENGTH_SHORT).show()
                        val user: FirebaseUser? = auth.currentUser
                        updateUI(user)
                    }
            }
        }
        putFile.addOnFailureListener {
            Toast.makeText(this, "Store Image Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "update UI")
    }
}
