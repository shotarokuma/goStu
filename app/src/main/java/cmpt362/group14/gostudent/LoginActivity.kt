package cmpt362.group14.gostudent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signIn(email, password)
        }
    }
    private fun signIn(email: String, password: String){
        val result = auth.signInWithEmailAndPassword(email, password)
        result.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                //update UI
                Log.d(TAG, "SignIn success")
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                updateUI(user)
            }else{
                //tell user sign in    private val TAG = "EMAIL_PASSWORD" failed, update UI
                Log.w(TAG, "SignIn failed", task.exception)
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }
    fun updateUI( user : FirebaseUser?){

    }
}