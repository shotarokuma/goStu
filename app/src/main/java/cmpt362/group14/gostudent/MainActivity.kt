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

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userNameEditText = findViewById(R.id.editTextUserName)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.loginButton)
        loginTextView = findViewById(R.id.loginTextView)
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            createAccount(email, password)
        }
        loginTextView.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email : String, password : String){

        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            Toast.makeText(
                this,
                "Please enter an email or password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val result = auth.createUserWithEmailAndPassword(email, password)
        result.addOnCompleteListener {  task ->
            if(task.isSuccessful){
                //update UI with account details
                Log.d(TAG, "CreateUser success, uid: ${task.result.user!!.uid}")
                Toast.makeText(this, "Create Account Successful", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                updateUI(user)
            } else{
                //tell user that authentication failed, update UI
                Log.w(TAG, "CreateUser fail", task.exception)
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "update UI")
    }
}
