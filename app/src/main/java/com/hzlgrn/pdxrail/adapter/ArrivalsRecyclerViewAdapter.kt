package com.hzlgrn.pdxrail.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isInvisible
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.repository.viewmodel.RecyclerViewItemModel
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
import com.hzlgrn.pdxrail.databinding.ItemArrivalBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

    private val content = mutableListOf<RecyclerViewItemModel>()
    fun setData(newData: List<RecyclerViewItemModel>) {
        val diffCallback = DiffUtilCallback(content, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        content.clear()
        content.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    var onClick = fun(_: ArrivalItemViewModel) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_arrival, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindItemViewModel(holder.binding, null)
        content?.get(position)?.let { contentData ->
            holder.dataJob = launch {
                flowData(contentData.uniqueId).collect { arrivalItemViewModel ->
                    withContext(Dispatchers.Main) {
                        onBindItemViewModel(holder.binding, arrivalItemViewModel)
                    }
                }
            }
        }
    }

    private fun onBindItemViewModel(binding: ItemArrivalBinding, itemViewModel: ArrivalItemViewModel?) {
        with (binding) {
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
        return content?.size ?: 0
    }

    class DiffUtilCallback(private val oldList: List<RecyclerViewItemModel>, private val newList: List<RecyclerViewItemModel>) :
        DiffUtil.Callback() {

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