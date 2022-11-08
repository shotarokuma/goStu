package cmpt362.group14.gostudent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Fragment that takes a user's email and password and signs them into firebase
 */
class EmailPasswordFragment : Fragment() {
    private val TAG = "EMAIL_PASSWORD"
    private lateinit var auth : FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var signInButton: Button
    private lateinit var createAccountButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init firebase auth
        auth = Firebase.auth
    }

    override fun onStop() {
        super.onStop()
        val currentUser = auth.currentUser
        if (currentUser != null){
            reload()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_password, container, false)
        // initialize elements of the view
        emailEditText = view.findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = view.findViewById(R.id.editTextTextPassword)
        cancelButton = view.findViewById(R.id.cancelBtn)
        signInButton = view.findViewById(R.id.signInBtn)
        createAccountButton = view.findViewById(R.id.createAccountBtn)

        //set button onClick Listeners
        cancelButton.setOnClickListener {
            //on cancel, close parent activity
            requireActivity().finish()
        }
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password= passwordEditText.text.toString()
            signIn(email, password)
        }
        createAccountButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password= passwordEditText.text.toString()
            createAccount(email, password)
        }
        return view
    }

    private fun createAccount(email : String, password : String){
        val result = auth.createUserWithEmailAndPassword(email, password)
        result.addOnCompleteListener {  task ->
            if(task.isSuccessful){
                //update UI with account details
                Log.d(TAG, "CreateUser success")
                val user = auth.currentUser
                updateUI(user)
            } else{
                //tell user that authentication failed, update UI
                Log.w(TAG, "CreateUser fail", task.exception)
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }
    /*
    Sign in existing user
     */
    private fun signIn(email: String, password: String){
        val result = auth.signInWithEmailAndPassword(email, password)
        result.addOnCompleteListener { task ->
           if(task.isSuccessful) {
               //update UI
               Log.d(TAG, "SignIn success")
               val user = auth.currentUser
               updateUI(user)
           }else{
               //tell user sign in    private val TAG = "EMAIL_PASSWORD" failed, update UI
               Log.w(TAG, "SignIn failed", task.exception)
               Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
               updateUI(null)
           }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload(){
        //reload UI, called when user is already logged in
    }
}