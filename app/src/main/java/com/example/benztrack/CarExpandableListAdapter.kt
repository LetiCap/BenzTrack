import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.benztrack.R

class CarExpandableListAdapter(
    private val context: Context,
    private val vehicleDescriptions: List<String>,
    private val vehicleDetails: List<List<Pair<String, String>>>
) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return vehicleDescriptions[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false // Indica che gli elementi figli non sono selezionabili
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
        var convertViewChild = convertView
        if (convertViewChild == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewChild = inflater.inflate(R.layout.list_item_vehicle, null)
        }
        val textTitleView = convertViewChild!!.findViewById<TextView>(R.id.textTitle)
        val textDetailsView = convertViewChild.findViewById<TextView>(R.id.textDetails)
        val details = vehicleDetails[groupPosition] // Otteniamo i dettagli corrispondenti al gruppo
        val detail = details[childPosition] // Otteniamo il dettaglio specifico per questo elemento
        textTitleView.text = "${detail.first}: ${detail.second}" // Visualizziamo chiave e valore
        textDetailsView.visibility = View.GONE // Nascondiamo il dettaglio aggiuntivo
        return convertViewChild
    }



    override fun getGroupCount(): Int {
        return vehicleDescriptions.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return vehicleDetails[groupPosition].size
    }
}
