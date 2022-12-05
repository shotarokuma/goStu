package cmpt362.group14.gostudent.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import cmpt362.group14.gostudent.MarketplaceAdapter
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.activity.AddItemActivity
import cmpt362.group14.gostudent.databinding.ActivityMarketplaceBinding
import cmpt362.group14.gostudent.model.Item
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MarketplaceFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var searchButton: Button
    private lateinit var searchBar: EditText
    private var _binding: ActivityMarketplaceBinding? = null
    private val binding get() = _binding!!
    private var itemList = ArrayList<Item>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        val root = binding.root
        searchBar = root.findViewById(R.id.search_bar)
        searchButton = root.findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            onSearch()
        }

        binding.addItemBtn.setOnClickListener() {
            val intent = Intent(requireContext(), AddItemActivity::class.java)
            startActivity(intent)
        }
        db = FirebaseFirestore.getInstance()
        fetchItems()
        return root
    }

    private fun fetchItems() {
        db.collection("item")
            .orderBy("createdTime")
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
                            binding.listviewItems.adapter =
                                MarketplaceAdapter(requireActivity(), itemList)
                        }
                        DocumentChange.Type.MODIFIED -> TODO("Not yet implemented")
                        DocumentChange.Type.REMOVED -> TODO("Not yet implemented")
                    }
                }
            }
    }

    private fun onSearch() {
        db.collection("item")
            .whereEqualTo("name", searchBar.text.toString())
            .get()
            .addOnSuccessListener {
                if (it.documents.size == 0) {
                    Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show()
                } else {
                    val item: Item = it.documents[0].toObject(Item::class.java)!!
                    itemList = arrayListOf()
                    itemList.add(item)
                    binding.listviewItems.adapter =
                        MarketplaceAdapter(requireActivity(), itemList)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
