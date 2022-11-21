package cmpt362.group14.gostudent.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Item(
    @DocumentId
    val iid: String = "",
    val price: Double = 0.0,
    val sellerId: String = "",
    val description: String = "",
    val displayImageUrl: String = "",
    val createdTime: Date = Date()
)
