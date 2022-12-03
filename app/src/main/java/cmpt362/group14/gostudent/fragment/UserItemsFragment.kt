package cmpt362.group14.gostudent.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cmpt362.group14.gostudent.ItemAdapter
import cmpt362.group14.gostudent.databinding.ActivityUserItemsBinding
import cmpt362.group14.gostudent.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class UserItemsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var binding: ActivityUserItemsBinding? = null
    private var itemList = ArrayList<Item>()
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityUserItemsBinding.inflate(layoutInflater)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()
        fetchItems()
        return binding!!.root
    }

    private fun fetchItems() {
        db.collection("item")
            .orderBy("createdTime")
            .whereEqualTo("sellerId",uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(HomeChatFragment.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val item: Item = dc.document.toObject(Item::class.java)
                            itemList.add(item)
                            binding!!.listviewItems.adapter =
                                ItemAdapter(requireActivity(), itemList)
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}