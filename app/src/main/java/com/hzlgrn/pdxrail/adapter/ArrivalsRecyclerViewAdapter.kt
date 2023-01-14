package com.hzlgrn.pdxrail.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.repository.viewmodel.UniqueIdModel
import com.hzlgrn.pdxrail.databinding.ItemArrivalBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class ArrivalsRecyclerViewAdapter(
    private val parentCC: CoroutineContext,
    private val flowData: (String) -> Flow<ArrivalItemViewModel>
    ) : RecyclerView.Adapter<ArrivalsRecyclerViewAdapter.ViewHolder>(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = parentCC + Dispatchers.IO
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemArrivalBinding = ItemArrivalBinding.bind(view)
        var dataJob: Job? = null
            set(newJob) {
                field?.cancel()
                field = newJob
            }
    }

    private val listUniqueIdModel = mutableListOf<UniqueIdModel>()
    fun setData(newData: List<UniqueIdModel>) {
        val diffCallback = DiffUtilCallback(listUniqueIdModel, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        listUniqueIdModel.clear()
        listUniqueIdModel.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    var onClick = fun(_: LatLng) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_arrival, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listUniqueIdModel.getOrNull(position).let { contentData ->
            holder.binding.root.isInvisible = contentData == null
            holder.binding.cardArrival.setOnClickListener {}
            contentData?.let {
                onBindItemViewModel(holder.binding, null)
                holder.dataJob = launch {
                    flowData(contentData.uniqueId).collect { arrivalItemViewModel ->
                        withContext(Dispatchers.Main) {
                            onBindItemViewModel(holder.binding, arrivalItemViewModel)
                        }
                    }
                }
            }
        }
    }

    private fun onBindItemViewModel(binding: ItemArrivalBinding, itemViewModel: ArrivalItemViewModel?) {
        with (binding) {
            cardArrival.setOnClickListener {
                itemViewModel?.latlng?.let { onClick(it) }
            }
            root.isInvisible = itemViewModel == null
            textShortSign.text = itemViewModel?.textShortSign ?: " "
            textScheduled.text = itemViewModel?.textScheduled ?: " "
            textEstimate.text = itemViewModel?.textEstimated ?: " "
            textEstimate.setTextColor(itemViewModel?.colorTextEstimated ?: Color.YELLOW)
            icArrival.isInvisible = itemViewModel == null
            icArrival.setImageResource(itemViewModel?.drawableArrivalMarker ?: R.drawable.marker_max_arrival)
            icArrival.rotation = itemViewModel?.drawableRotation ?: 0f
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.dataJob = null
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return listUniqueIdModel.size
    }

    class DiffUtilCallback(
        private val oldList: List<UniqueIdModel>,
        private val newList: List<UniqueIdModel>
    ) : DiffUtil.Callback() {

        // old size
        override fun getOldListSize(): Int = oldList.size

        // new list size
        override fun getNewListSize(): Int = newList.size

        // if items are same
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.javaClass == newItem.javaClass
        }

        // check if contents are same
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.uniqueId == newItem.uniqueId
        }
    }

}