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
import com.google.android.material.composethemeadapter.MdcTheme
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.databinding.FramedComposableBinding
import java.text.SimpleDateFormat
import java.util.*

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
           MdcTheme {
               ArrivalEmptyViewCard()
           }
       }
    }

    private fun bind(binding: FramedComposableBinding, position: Int) {
        getItem(position)?.let { viewModel ->
            binding.composable.setContent {
                MdcTheme {
                    ArrivalItemViewCard(viewModel)
                }
            }
        }
    }

    @Composable
    private fun ArrivalEmptyViewCard() {
        val textMax = stringResource(id = R.string.content_description_max_stop_verbose)
        val textStreetcar = stringResource(id = R.string.content_description_streetcar_stop_verbose)
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 3.dp, bottom = 3.dp, end = 8.dp)
            ) {
                Card(
                    elevation = 3.dp,
                    modifier = Modifier
                        .clickable {}
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.marker_max_stop
                            ),
                            contentDescription = "Max stop",
                            modifier = Modifier
                                .height(36.dp)
                                .width(36.dp)
                        )
                        Text(
                            text = textMax,
                            modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 3.dp, bottom = 3.dp, end = 8.dp)
            ) {
                Card(
                    elevation = 3.dp,
                    modifier = Modifier
                        .clickable {}
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.marker_streetcar_stop
                            ),
                            contentDescription = "Streetcar stop",
                            modifier = Modifier
                                .height(36.dp)
                                .width(36.dp)
                        )
                        Text(
                            text = textStreetcar,
                            modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)
                        )
                    }

                }
            }
        }
    }
    
    @Composable
    private fun ArrivalItemViewCard(viewModel: ArrivalItemViewModel) {
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 3.dp, bottom = 3.dp, end = 8.dp)
        ) {
            Card(
                elevation = 3.dp,
                modifier = Modifier
                        .clickable {
                            onItemClickCallback(viewModel)
                        }
                        .fillMaxWidth(),
                shape = RoundedCornerShape(6.dp)
            ) {
                BoxWithConstraints {
                    val constraints = cardContentConstraints()
                    ConstraintLayout(constraints) {
                        Text(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier
                                    .layoutId("textShortSign")
                                    .padding(start = 8.dp, top = 3.dp, end = 8.dp),
                            overflow = TextOverflow.Ellipsis,
                            text = viewModel.textShortSign,
                        )

                        Image(
                            painter = painterResource(
                                id = viewModel.drawableArrivalMarker
                            ),
                            contentDescription = "Arrival",
                            modifier = Modifier
                                    .height(36.dp)
                                    .layoutId("icArrival")
                                    .rotate(viewModel.drawableRotation)
                                    .width(36.dp)
                        )

                        val textScheduled = if (viewModel.scheduled == 0L) {
                            context.getString(R.string.no_arrival)
                        } else {
                            SimpleDateFormat("h:mma", Locale.US).format(Date(viewModel.scheduled)).let {
                                context.getString(R.string.arriving_at).format(it)
                            }
                        }
                        Text(
                            fontSize = 12.sp,
                            maxLines = 1,
                            modifier = Modifier
                                    .layoutId("textScheduled")
                                    .padding(top = 3.dp),
                            overflow = TextOverflow.Ellipsis,
                            text = textScheduled,
                        )

                        if (viewModel.estimated == 0L
                            || (viewModel.scheduled > 0L
                                    && viewModel.estimated > 0L
                                    && viewModel.scheduled in (viewModel.estimated - RANGE_ON_TIME_MS) .. (viewModel.estimated + RANGE_ON_TIME_MS))) {
                            // NO TEXT ESTIMATE
                        } else {
                            val textEstimate = context
                                .getString(R.string.estimated_at)
                                .format(SimpleDateFormat("h:mm:ss", Locale.US)
                                        .format(Date(viewModel.estimated)))
                            val isLate = viewModel.estimated > viewModel.scheduled
                            val textEstimateColorId = if (isLate)
                                    R.color.max_red_line
                                else
                                    R.color.max_green_line

                            Text(
                                color = colorResource(id = textEstimateColorId),
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier
                                        .layoutId("textEstimate")
                                        .padding(bottom = 3.dp),
                                overflow = TextOverflow.Ellipsis,
                                text = textEstimate,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun cardContentConstraints(): ConstraintSet {
        return ConstraintSet {
            val textShortSign = createRefFor("textShortSign")
            val icArrival = createRefFor("icArrival")
            val textScheduled = createRefFor("textScheduled")
            val textEstimate = createRefFor("textEstimate")

            constrain (textShortSign) {
                top.linkTo(parent.top)
            }
            constrain (icArrival) {
                start.linkTo(parent.start)
                top.linkTo(textShortSign.bottom)
            }
            constrain (textScheduled) {
                top.linkTo(textShortSign.bottom)
                start.linkTo(icArrival.end)
            }
            constrain (textEstimate) {
                top.linkTo(textScheduled.bottom)
                start.linkTo(icArrival.end)
            }
        }
    }

}
