package cmpt362.group14.gostudent.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class ProfileSettingsFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile_settings, null)

        userNameEditText = view.findViewById(R.id.editTextUserName)
        emailEditText = view.findViewById(R.id.editTextEmailAddress)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        saveButton = view.findViewById(R.id.save_changes)
        cancelButton = view.findViewById(R.id.cancel_changes)
        profileImageButton = view.findViewById(R.id.profileImageButton)

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
        return view
    }

    private fun updateAccount(name: String, email: String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please enter an email or password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (galleryImgUri == null) {
            Toast.makeText(
                requireContext(),
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
//                        Toast.makeText(requireContext(), "Create Account Successful", Toast.LENGTH_SHORT).show()
//                        val user: FirebaseUser? = auth.currentUser
//                        updateUI(user)
//                    }
//            }
        }
        putFile.addOnFailureListener {
            Toast.makeText(requireContext(), "Store Image Failed", Toast.LENGTH_SHORT).show()
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
                    Picasso.get().load(user.profileImageUrl).into(profileImageButton)
                }

            }
            .addOnFailureListener { exception ->
                Log.w(HomeChatFragment.TAG, "Error getting documents: ", exception)
            }
    }


}