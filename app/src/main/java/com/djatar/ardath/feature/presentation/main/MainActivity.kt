package com.djatar.ardath.feature.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.navigation.compose.rememberNavController
import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.core.presentation.components.AppBarContainer
import com.djatar.ardath.core.presentation.components.AppEntry
import com.djatar.ardath.ui.theme.ArdathTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            view.setPadding(0,0,0,0)
            windowInsets
        }
        enableEdgeToEdge()

        context = baseContext

        setContent {
            ArdathTheme {
                val navController = rememberNavController()
                val bottomBarState = rememberSaveable { mutableStateOf(true) }
                val isScrolling = remember { mutableStateOf(false) }
                val hasLogin = remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppBarContainer(
                        navController = navController,
                        bottomBarState = bottomBarState.value,
                        isScrolling = isScrolling.value,
                        hasLogin = hasLogin.value
                    ) {
                        AppEntry(
                            navController = navController,
                            paddingValues = innerPadding,
                            bottomBarState = bottomBarState,
                            isScrolling = isScrolling,
                            hasLogin = hasLogin
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}