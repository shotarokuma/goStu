package cmpt362.group14.gostudent.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class Response(
    code: Int
)
interface ApiService {
    @GET(".")
    fun sendMessage(
        @Query("token") token: String,
        @Query("body") body: String,
        @Query("title") title: String
    ): Call<Response>
}
