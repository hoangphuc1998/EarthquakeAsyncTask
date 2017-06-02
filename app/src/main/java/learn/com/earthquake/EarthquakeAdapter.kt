package learn.com.earthquake

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Administrator on 5/24/2017.
 */

class EarthquakeAdapter(activity:Context,list: ArrayList<Event>) : RecyclerView.Adapter<EarthquakeAdapter.MyViewHolder>(),Filterable {
    override fun getFilter(): Filter {
        return EventFilter()
    }

    var earthquakeList=list
    var firstEarthquakeList =list
    val context=activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeAdapter.MyViewHolder? {
        val view:View=LayoutInflater.from(context).inflate(R.layout.item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: EarthquakeAdapter.MyViewHolder, position: Int) {
        val txtMag= holder.itemView.findViewById(R.id.txtMag) as TextView
        val txtPlace=holder.itemView.findViewById(R.id.txtPlace) as TextView
        val txtDate=holder.itemView.findViewById(R.id.txtDate) as TextView
        val event=earthquakeList.get(position)
        txtMag.text = event.mag.toString()
        txtPlace.text = event.place
        val sdf=SimpleDateFormat("dd/MM/yyyy")
        val cal=Calendar.getInstance()
        cal.timeInMillis=event.time.toLong()
        txtDate.text = sdf.format(cal.time)
    }

    override fun getItemCount(): Int {
        return earthquakeList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    inner class EventFilter : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filterResult=FilterResults()
            if (constraint==null){
                filterResult.values= firstEarthquakeList
                filterResult.count= firstEarthquakeList.size
            }
            else{
                var earthquakeFilter=ArrayList<Event>()
                for (event in firstEarthquakeList){
                    if (event.place.contains(constraint)){
                        earthquakeFilter.add(event)
                    }
                }
                filterResult.values=earthquakeFilter
                filterResult.count=earthquakeFilter.size
            }
            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            earthquakeList=results.values as ArrayList<Event>
            notifyDataSetChanged()
        }

    }
}
