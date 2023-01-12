package com.hzlgrn.pdxrail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.databinding.FramedComposableBinding

class ArrivalModelArrayAdapter(context: Context, viewModel: List<ArrivalItemViewModel>?)
    : ArrayAdapter<ArrivalItemViewModel>(context, 0, viewModel ?: emptyList()) {

    companion object {
        const val ITEM_TYPE_EMPTY = 0
        const val ITEM_TYPE_ITEM = 1

        const val RANGE_ON_TIME_MS = 30000L // within 30 seconds before and after scheduled
    }

    var onItemClickCallback = fun(_: ArrivalItemViewModel) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when(getItemViewType(position)) {
            ITEM_TYPE_EMPTY -> {
                convertView ?: FramedComposableBinding
                        .inflate(LayoutInflater.from(context))
                        .also { binding ->
                            bind(binding)
                        }.root
            }
            else -> {
                FramedComposableBinding
                    .inflate(LayoutInflater.from(context))
                    .also { binding ->
                        bind(binding, position)
                    }.root
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

    private fun bind(binding: FramedComposableBinding) {
       binding.composable.setContent {
           AppCompatTheme {
               ComposableItems.ArrivalEmptyViewCard()
           }
       }
    }

    private fun bind(binding: FramedComposableBinding, position: Int) {
        getItem(position)?.let { viewModel ->
            binding.composable.setContent {
                AppCompatTheme {
                    ComposableItems.ArrivalItemViewCard(context, viewModel, onItemClickCallback)
                }
            }
        }
    }

}
