package cmpt362.group14.gostudent.fragment

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileSettingsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var user: User
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profileImageButton: ImageButton
    private var galleryImgUri: Uri? = null

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
        storage = FirebaseStorage.getInstance()

        fetchUserData()

        galleryResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                galleryImgUri = it.data?.data
                profileImageButton.setImageURI(galleryImgUri)
            }
        }

        profileImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryResult.launch(intent)
        }

        saveButton.setOnClickListener {
            val name: String = userNameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            updateUser(name, email, password)
        }
        return view
    }

    private fun updateUser(newName: String, newEmail: String, newPassword: String) {
        val fname = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$fname")
        if (galleryImgUri == null) {
            val newUser = User(
                id = user.id,
                uid = user.uid,
                name = newName,
                password = newPassword,
                mail = newEmail,
                profileImageUrl = user.profileImageUrl
            )
            db.collection("user")
                .document(user.id)
                .set(newUser)
                .addOnSuccessListener {
                    Toast.makeText(context, "Update Account Successful", Toast.LENGTH_SHORT).show()
                }
        } else {
            val putFile = ref.putFile(galleryImgUri!!)
            putFile.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val newUser = User(
                        id = user.id,
                        uid = user.uid,
                        name = newName,
                        password = newPassword,
                        mail = newEmail,
                        profileImageUrl = it.toString()
                    )
                    db.collection("user")
                        .document(user.id)
                        .set(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Update Account Successful", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            putFile.addOnFailureListener {
                Toast.makeText(context, "Store Image Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserData() {
        db.collection("user")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener {
                user = it.documents[0].toObject(User::class.java)!!
                userNameEditText.setText(user.name)
                emailEditText.setText(user.mail)
                passwordEditText.setText(user.password)
                Picasso.get().load(user.profileImageUrl).into(profileImageButton)
            }
            .addOnFailureListener { exception ->
                Log.w(HomeChatFragment.TAG, "Error getting documents: ", exception)
            }
    }
}
