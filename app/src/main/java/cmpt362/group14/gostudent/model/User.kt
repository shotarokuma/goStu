package cmpt362.group14.gostudent.model

import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val fcm : String = "",
    val password: String = "",
    val mail: String = "",
    var createdTime: Date = Date(),
    val profileImageUrl: String = ""
)
