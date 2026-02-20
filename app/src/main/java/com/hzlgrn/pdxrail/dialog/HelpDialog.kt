package com.hzlgrn.pdxrail.dialog

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.databinding.FramedComposableBinding
import com.hzlgrn.pdxrail.theme.PdxRailTheme

class HelpDialog : DialogFragment() {

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
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        binding.composable.setContent {
            PdxRailTheme {
                DialogHelpContent()
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
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = resources.getString(R.string.help_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.marker_max_stop),
                    contentDescription = "MAX stop"
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = resources.getString(R.string.help_max_stop),
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.marker_streetcar_stop),
                    contentDescription = "Streetcar stop"
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = resources.getString(R.string.help_streetcar_stop),
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.marker_max_arrival),
                    contentDescription = "Vehicle arrival"
                )
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = resources.getString(R.string.help_arrival),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 3.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterStart),
                    text = resources.getString(R.string.permission_location_reason),
                )
                Button(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = { dialog?.dismiss() },
                ) {
                    Text(
                            text = resources.getString(R.string.dialog_help_action),
                    )
                }
            }
        }
    }

}