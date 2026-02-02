package com.hzlgrn.pdxrail.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.compose.PdxRailDrawer
import com.hzlgrn.pdxrail.data.room.ApplicationRoomLoader
import com.hzlgrn.pdxrail.databinding.ActivityPdxRailBinding
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PdxRailActivity : AppCompatActivity() {
    @Inject
    lateinit var applicationRoomLoader: ApplicationRoomLoader
    private val pdxRailViewModel: PdxRailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val drawerOpen by pdxRailViewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()

                    var selectedMenu by remember { mutableStateOf("composers") }
                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                pdxRailViewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    val scope = rememberCoroutineScope()
                    PdxRailDrawer(
                        drawerState = drawerState,
                        selectedMenu = selectedMenu,
                        onArrivalClick = {},
                        onReviewClick = {},
                    ) {
                        AndroidViewBinding(ActivityPdxRailBinding::inflate)
                    }
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        launchLoadData()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun launchLoadData() {
        pdxRailViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                applicationRoomLoader.load()
            }
        }
    }
}