import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.benztrack.R

class CarExpandableListAdapter(
    private val context: Context,
    private val vehicleDescriptions: List<String>,
    private val vehicleDetails: List<List<Pair<String, String>>>,
    private val vehicleIds: List<Int>,
    private val CO2:Int
) : BaseExpandableListAdapter() {
    private val database: DatabaseApp = DatabaseApp(context)

    override fun getGroup(groupPosition: Int): Any {
        return vehicleDescriptions[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true // Indica che gli elementi figli non sono selezionabili
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
        textView.text = vehicleDescriptions[groupPosition]
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
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
            convertViewGroup = inflater.inflate(R.layout.list_item_vehicle, null)
        }

        val details = vehicleDetails[groupPosition] // Otteniamo i dettagli corrispondenti al gruppo
        val detail = details[childPosition] // Otteniamo il dettaglio specifico per questo elemento

        // Se l'elemento è l'ID, non visualizzarlo

            // Se non è l'ID, visualizziamo normalmente i dettagli
            val textTitleView = convertViewGroup!!.findViewById<TextView>(R.id.textTitle)
            textTitleView.text = "${detail.first}: ${detail.second}" // Visualizziamo chiave e valore
            convertViewGroup.visibility = View.VISIBLE

            // Aggiungiamo il listener di clic al convertViewGroup
            convertViewGroup.setOnClickListener {
                // Otteniamo l'ID
                val id = vehicleIds[groupPosition]
                database.insertCar(id, CO2)
                database.createTableInfoVehicle(id.toString())
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
