/*
The SleepNightAdapter adapts the list of SleepNight objects
 into something RecyclerView can use and display.

 The SleepNightAdapter adapter produces ViewHolders that contain
  the views, data, and meta information for the recycler view to display the data.

  RecyclerView uses the SleepNightAdapter to determine how many items
   there are to display (getItemCount()).
    RecyclerView uses onCreateViewHolder()
   and onBindViewHolder() to get view holders bound to data for displaying.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM =  1

class SleepNightAdapter(val clickListener: SleepNightListener): ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //display the data for one list item at the specified position
        when (holder){
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
            else -> throw Exception("Uknown viewType ${position}")
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (getItem(viewType)) {
            is DataItem.Header -> TextViewHolder.from(parent)
            is DataItem.SleepNightItem -> ViewHolder.from(parent)
            else -> throw ClassCastException("Uknown viewType ${viewType}")
        }
    }

    fun addHeaderAndSubmitList(list: List<SleepNight>?){
        adapterScope.launch{
            val items = when (list){
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header)+ list.map {DataItem.SleepNightItem(it)}
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){

        public fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}


class SleepNightListener(val clickListener: (sleepId: Long) -> Unit){
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem{
    abstract val id: Long
    data class SleepNightItem(val sleepNight: SleepNight): DataItem(){

        override  val id = sleepNight.nightId
    }

    object Header: DataItem() {
        override val id = Long.MIN_VALUE
    }
}

