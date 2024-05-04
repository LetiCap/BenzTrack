package com.example.benztrack

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class CarExpandableListAdapter(
    private val context: Context,
    private val carData: MutableList<Pair<String, MutableList<Pair<String, String>>>>
) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return carData[groupPosition].first
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertViewGroup = convertView
        if (convertViewGroup == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewGroup = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null)
        }
        val textView = convertViewGroup!!.findViewById<TextView>(android.R.id.text1)
        textView.text = carData[groupPosition].first
        textView.setTextColor(Color.BLACK) // Imposta il colore del testo a nero
        return convertViewGroup
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return carData[groupPosition].second[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertViewChild = convertView
        if (convertViewChild == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewChild = inflater.inflate(android.R.layout.simple_expandable_list_item_2, null)
        }
        val item = getChild(groupPosition, childPosition) as Pair<String, String>
        val textView1 = convertViewChild!!.findViewById<TextView>(android.R.id.text1)
        val textView2 = convertViewChild.findViewById<TextView>(android.R.id.text2)
        textView1.text = item.first
        textView2.text = item.second
        textView1.setTextColor(Color.BLACK) // Imposta il colore del testo a nero
        textView2.setTextColor(Color.BLACK) // Imposta il colore del testo a nero
        return convertViewChild
    }

    override fun getGroupCount(): Int {
        return carData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return carData[groupPosition].second.size
    }
}
