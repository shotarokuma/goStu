package cmpt362.group14.gostudent.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private val TAG = "SignUpActivity"
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var profileImageButton: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var newUser: User
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userNameEditText = findViewById(R.id.editTextUserName)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.loginButton)
        loginTextView = findViewById(R.id.loginTextView)
        profileImageButton = findViewById(R.id.profileImageButton)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        galleryResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Image Selected")
                val galleryImgUri = it.data?.data!!
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
        newUser = User(uid = uid, name = name, mail = email, password = password)
        db.collection("user")
            .document()
            .set(newUser)
            .addOnSuccessListener {
                Toast.makeText(this, "Create Account Successful", Toast.LENGTH_SHORT).show()
                val user: FirebaseUser? = auth.currentUser
                updateUI(user)
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "update UI")
    }
}
