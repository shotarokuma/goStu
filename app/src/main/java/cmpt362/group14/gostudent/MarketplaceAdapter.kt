package cmpt362.group14.gostudent

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import cmpt362.group14.gostudent.model.Item

class MarketplaceAdapter(private val context: Activity, private val arrayList: ArrayList<Item>) : ArrayAdapter<Item>(context,
    R.layout.list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_item, null)

        val imageView : ImageView = view.findViewById(R.id.imageviewItem)
        val textViewItemName : TextView = view.findViewById(R.id.itemName)
        val textViewPrice : TextView = view.findViewById(R.id.itemPrice)


        //imageView.setImageResource(arrayList[position].displayImageUrl)
        textViewItemName.text = arrayList[position].name
        textViewPrice.text = arrayList[position].price.toString()


        return view
    }
}