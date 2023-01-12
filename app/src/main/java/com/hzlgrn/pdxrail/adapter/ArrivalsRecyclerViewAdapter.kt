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

class ArrivalsRecyclerViewAdapter(): RecyclerView.Adapter<ArrivalsRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val composable: ComposeView = view.findViewById(R.id.composable)
    }

    var content: List<ArrivalItemViewModel>? = null
        set(newContent) {
            field = newContent
            notifyDataSetChanged() // DiffUtils!
        }
    var onClick = fun(_: ArrivalItemViewModel) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.framed_composable, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.composable.setContent {
            MaterialTheme(
                colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
            ) {
                content?.get(position)?.let { contentData ->
                    ComposableItems.ArrivalItemViewCard(
                        context = holder.itemView.context,
                        viewModel = contentData,
                        onClick = onClick)
                } ?: ComposableItems.ArrivalEmptyViewCard()
            }
        }
    }

    override fun getItemCount(): Int {
        return content?.size ?: 0
    }


}