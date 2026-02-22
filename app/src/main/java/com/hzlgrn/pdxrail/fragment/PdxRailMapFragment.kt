package com.hzlgrn.pdxrail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hzlgrn.pdxrail.compose.pdxrail.PdxRailMap
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdxRailMapFragment : Fragment() {

    private val pdxRailViewModel: PdxRailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            PdxRailTheme {
                PdxRailMap(pdxRailViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pdxRailViewModel.flowRailSystemMap()
        pdxRailViewModel.loadMapIcon()
    }
}