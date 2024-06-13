package com.example.benztrack
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat


class CarExpandableListAdapter(
    private val context: Context,
    private val vehicleDescriptions: List<String>,
    private val vehicleDetails: List<List<Pair<String, String>>>,
    private val vehicleModel: List<String>,
    private val CO2: Double
) : BaseExpandableListAdapter() {

    private val database: DatabaseApp = DatabaseApp(context)

    override fun getGroup(groupPosition: Int): Any {
        return vehicleDescriptions[groupPosition]
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
            convertViewGroup = inflater.inflate(R.layout.list_item_vehicle, null)
        }
        val textView = convertViewGroup!!.findViewById<TextView>(R.id.textTitle)
        textView.text = vehicleDescriptions[groupPosition]
        convertViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDutchWhite))
        return convertViewGroup
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return vehicleDetails[groupPosition]
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
        var convertViewGroup = convertView
        if (convertViewGroup == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewGroup = inflater.inflate(R.layout.expandable_list_child_item, null)
        }

        val details = vehicleDetails[groupPosition]
        val detail = details[childPosition]

        val textTitleView = convertViewGroup!!.findViewById<TextView>(R.id.textTitle)
        val textDetailsView = convertViewGroup.findViewById<TextView>(R.id.textDetails)
        textTitleView.text = detail.first
        textDetailsView.text = detail.second
        convertViewGroup.visibility = View.VISIBLE

        convertViewGroup.setOnClickListener {
            val model = vehicleModel[groupPosition]
            Toast.makeText(context, "Hai selezionato il modello: $model", Toast.LENGTH_SHORT).show()

            database.insertCar(model, CO2)
            database.createTableInfoVehicle(model)
        }

        return convertViewGroup
    }

    override fun getGroupCount(): Int {
        return vehicleDescriptions.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return vehicleDetails[groupPosition].size
    }
}
