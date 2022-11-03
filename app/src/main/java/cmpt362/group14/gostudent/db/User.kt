package cmpt362.group14.gostudent.db

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
)
