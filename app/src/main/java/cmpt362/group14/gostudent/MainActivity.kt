package cmpt362.group14.gostudent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import cmpt362.group14.gostudent.model.User

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
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
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
                storeAccount(task.result.user!!.uid,name, email, password)
            } else {
                // tell user that authentication failed, update UI
                Log.w(TAG, "CreateUser fail", task.exception)
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    private fun storeAccount(uid: String, name: String, email: String, password:String){
        newUser = User(uid, name, email, password)
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
