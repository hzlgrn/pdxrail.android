package com.hzlgrn.pdxrail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.databinding.ItemArrivalBinding
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import java.text.SimpleDateFormat
import java.util.*

class ArrivalModelArrayAdapter(context: Context, viewModel: List<ArrivalItemViewModel>?)
    : ArrayAdapter<ArrivalItemViewModel>(context, 0, viewModel ?: emptyList()) {

    companion object {
        const val ITEM_TYPE_EMPTY = 0
        const val ITEM_TYPE_ITEM = 1

        const val RANGE_ON_TIME_MS = 30000L // within 30 seconds before and after scheduled
    }

    private var mItemArrivalBinding: ItemArrivalBinding? = null

    var onItemClickCallback = fun(_: ArrivalItemViewModel) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when(getItemViewType(position)) {
            ITEM_TYPE_EMPTY -> {
                convertView ?: LayoutInflater.from(context).inflate(R.layout.item_arrival_hint, parent, false)
            }
            else -> {
                mItemArrivalBinding = ItemArrivalBinding.inflate(LayoutInflater.from(context)).also {
                    bind(it, getItem(position))
                }
                mItemArrivalBinding!!.root
            }
        }
    }

    override fun getCount(): Int {
        val count = super.getCount()
        return if (count < 1) 1 else count
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        val count = super.getCount()
        return when {
            count < 1 -> ITEM_TYPE_EMPTY
            else -> ITEM_TYPE_ITEM
        }
    }

    private fun bind(binding: ItemArrivalBinding, model: ArrivalItemViewModel?) {
        model?.let { bindWithData(binding, model) }
        if (model == null) bindWithoutData(binding)
    }

    private fun bindWithData(binding: ItemArrivalBinding, model: ArrivalItemViewModel) {
        binding.textShortSign.text = model.textShortSign
        binding.textScheduled.text = if (model.scheduled == 0L) {
            context.getString(R.string.no_arrival)
        } else {
            SimpleDateFormat("h:mma", Locale.US).format(Date(model.scheduled)).let {
                context.getString(R.string.arriving_at).format(it)
            }
        }
        if (model.estimated == 0L
            || (model.scheduled > 0L
                && model.estimated > 0L
                && model.scheduled in (model.estimated - RANGE_ON_TIME_MS) .. (model.estimated + RANGE_ON_TIME_MS))) {
            binding.textEstimate.visibility = View.GONE
        } else {
            binding.textEstimate.text = context.getString(R.string.estimated_at).format(
                    SimpleDateFormat("h:mm:ss", Locale.US).format(
                        Date(model.estimated)))
            if (model.estimated > model.scheduled) {
                binding.textEstimate.setTextColor(
                        ResourcesCompat
                                .getColor(binding.root.resources, R.color.max_red_line, context.theme))
            } else {
                binding.textEstimate.setTextColor(
                        ResourcesCompat
                                .getColor(binding.root.resources, R.color.max_green_line, context.theme))
            }
            binding.textEstimate.visibility = View.VISIBLE
        }
        binding.icArrival.apply {
            setImageResource(model.drawableArrivalMarker)
            rotation = model.drawableRotation
        }
        binding.root.setOnClickListener {
            onItemClickCallback(model)
        }
        binding.root.visibility = View.VISIBLE
    }

    private fun bindWithoutData(binding: ItemArrivalBinding) {
        binding.root.visibility = View.INVISIBLE
    }

}
