package cmpt362.group14.gostudent

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cmpt362.group14.gostudent.ItemAdapter.Companion.ITEM_KEY
import cmpt362.group14.gostudent.activity.EditItemActivity
import cmpt362.group14.gostudent.model.Item
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ItemAdapter(private val context: Activity, private val arrayList: ArrayList<Item>) : ArrayAdapter<Item>(
    context,
    R.layout.list_item, arrayList
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_item, null)

        val imageView: ImageView = view.findViewById(R.id.imageviewItem)
        val textViewItemName: TextView = view.findViewById(R.id.itemName)
        val textViewPrice: TextView = view.findViewById(R.id.itemPrice)

        val item = arrayList[position]
        textViewItemName.text = item.name
        textViewPrice.text = item.price.toString()
        Picasso.get().load(item.displayImageUrl).into(imageView)

        view.setOnClickListener() {
            val intent = Intent(context, EditItemActivity::class.java)
            intent.putExtra(EditItemActivity.ITEM_KEY, Gson().toJson(item))
            ContextCompat.startActivity(context, intent, null)
        }
        return view
    }

    companion object {
        val ITEM_KEY = "ITEM_KEY"
    }
}
