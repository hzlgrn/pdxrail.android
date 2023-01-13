package com.hzlgrn.pdxrail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.repository.viewmodel.RecyclerViewItemModel
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
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
        var dataJob: Job? = null
            set(newJob) {
                field?.cancel()
                field = newJob
            }
        val composable: ComposeView = view.findViewById(R.id.composable)
    }

    var content: List<RecyclerViewItemModel>? = null
        set(newContent) {
            field = newContent
            notifyDataSetChanged() // DiffUtils!
            // It will always be more efficient to use more specific change events if you can.
            // Rely on notifyDataSetChanged as a last resort.
        }
    var onClick = fun(_: ArrivalItemViewModel) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.framed_composable, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.composable.disposeComposition()
        content?.get(position)?.let { recyclerViewItemModel ->
            holder.composable.setContent {
                MaterialTheme(
                    colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
                ) {
                    ComposableItems.ArrivalItemLoadingViewCard(holder.itemView.context)
                }
            }
            holder.dataJob = launch {
                flowData(recyclerViewItemModel.uniqueId).collect { arrivalItem ->
                    withContext(Dispatchers.Main) {
                        bind(holder, arrivalItem)
                    }
                }
            }
        } ?: run {
            holder.composable.setContent {
                MaterialTheme(
                    colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
                ) {
                    ComposableItems.ArrivalEmptyViewCard()
                }
            }
        }
    }

    private fun bind(holder: ViewHolder, arrivalItem: ArrivalItemViewModel) {
        holder.composable.disposeComposition()
        holder.composable.setContent {
            MaterialTheme(
                colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
            ) {
                ComposableItems.ArrivalItemViewCard(
                    context = holder.itemView.context,
                    viewModel = arrivalItem,
                    onClick = onClick)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.dataJob = null
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return content?.size ?: 0
    }


}