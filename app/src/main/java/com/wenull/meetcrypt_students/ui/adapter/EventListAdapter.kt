package com.wenull.meetcrypt_students.ui.adapter

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.databinding.EventListLayoutBinding
import com.wenull.meetcrypt_students.databinding.TeacherListLayoutBinding
import com.wenull.meetcrypt_students.utils.helpers.ObjectSerializer
import com.wenull.meetcrypt_students.utils.models.Event
import com.wenull.meetcrypt_students.utils.models.StudentCredentials
import java.util.*
import kotlin.collections.ArrayList

class EventListAdapter(private val context: Context, private val studentCredentials: StudentCredentials) : RecyclerView.Adapter<EventListHolder>(), Filterable{


    val eventList: ArrayList<Event> = ArrayList()
    val eventListAll: ArrayList<Event> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<EventListLayoutBinding>(inflater, R.layout.event_list_layout, parent, false)
        return EventListHolder(binding, context)
    }

    override fun getItemCount(): Int = eventList.size

    override fun onBindViewHolder(holder: EventListHolder, position: Int) {
        holder.bind(eventList[position], studentCredentials)
    }

    fun setList(eventList: List<Event>){
        this.eventList.clear()
        this.eventList.addAll(eventList)
        this.eventListAll.clear()
        this.eventListAll.addAll(eventList)
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return filter
    }

    private var filter = object: Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val result = FilterResults()
            val filteredList = ArrayList<Event>()

            filteredList.addAll(eventListAll)
            result.values = filteredList

            if(constraint != null){
                result.values = filteredList.filter {
                it.eventName.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(
                    Locale.ROOT
                )
                )
                }
            }else if(constraint?.length == 0){
                eventList.addAll(eventListAll)
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            eventList.clear()
            eventList.addAll(results?.values as List<Event>)
            notifyDataSetChanged()
        }
    }
}


class EventListHolder(private val binding: EventListLayoutBinding, private val context: Context): RecyclerView.ViewHolder(binding.root){
    fun bind(event: Event, studentCredentials: StudentCredentials){

        binding.textViewEventName.text = event.eventName.toString()
        binding.textViewUserRandomName.text = event.randomNames.get(studentCredentials.uid)

        binding.buttonCopyRandomName.setOnClickListener{
            val clipboardManager: ClipboardManager = context.getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("randomUsername", binding.textViewUserRandomName.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}