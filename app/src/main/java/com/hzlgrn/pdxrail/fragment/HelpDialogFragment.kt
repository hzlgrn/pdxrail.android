package com.hzlgrn.pdxrail.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.hzlgrn.pdxrail.databinding.FramedComposableBinding
import com.hzlgrn.pdxrail.theme.PdxRailTheme

class HelpDialogFragment : DialogFragment() {

    var onDismissListener: (() -> Unit)? = null

    private lateinit var binding: FramedComposableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FramedComposableBinding.inflate(inflater)
        dialog?.apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        binding.composable.setContent {
            PdxRailTheme {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                ) {
                    DialogHelpContent()
                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.setOnDismissListener {
            dialog?.dismiss()
            onDismissListener?.invoke()
        }
    }

    @Composable
    private fun DialogHelpContent() {
        Column(
            modifier = Modifier.Companion
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.Companion.padding(bottom = 6.dp),
                text = resources.getString(com.hzlgrn.pdxrail.R.string.help_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = com.hzlgrn.pdxrail.R.drawable.marker_max_stop),
                    contentDescription = "MAX stop"
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = resources.getString(com.hzlgrn.pdxrail.R.string.help_max_stop),
                )
            }
            Row(
                modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = com.hzlgrn.pdxrail.R.drawable.marker_streetcar_stop),
                    contentDescription = "Streetcar stop"
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = resources.getString(com.hzlgrn.pdxrail.R.string.help_streetcar_stop),
                )
            }
            Row(
                modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = com.hzlgrn.pdxrail.R.drawable.marker_max_arrival),
                    contentDescription = "Vehicle arrival"
                )
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = resources.getString(com.hzlgrn.pdxrail.R.string.help_arrival),
                )
            }
            Box(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 3.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.Companion
                        .fillMaxWidth(0.6f)
                        .align(Alignment.Companion.CenterStart),
                    text = resources.getString(com.hzlgrn.pdxrail.R.string.permission_location_reason),
                )
                Button(
                    modifier = Modifier.Companion.align(Alignment.Companion.BottomEnd),
                    onClick = { dialog?.dismiss() },
                ) {
                    Text(
                        text = resources.getString(com.hzlgrn.pdxrail.R.string.dialog_help_action),
                    )
                }
            }
        }
    }

}