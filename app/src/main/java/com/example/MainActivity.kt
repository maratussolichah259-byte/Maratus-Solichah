package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.GroupProjectScreen
import com.example.ui.screens.MaterialScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.StemGuideScreen
import com.example.ui.screens.StudentHomeScreen
import com.example.ui.screens.StudentLoginScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.screens.TeacherLoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StemViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val viewModel: StemViewModel = viewModel(
        factory = StemViewModel.provideFactory(context)
    )

    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            AppScreen.ROLE_SELECTION -> RoleSelectionScreen(viewModel, screenModifier)
            AppScreen.STUDENT_LOGIN -> StudentLoginScreen(viewModel, screenModifier)
            AppScreen.TEACHER_LOGIN -> TeacherLoginScreen(viewModel, screenModifier)
            AppScreen.STUDENT_HOME -> StudentHomeScreen(viewModel, screenModifier)
            AppScreen.STUDENT_MATERIAL -> MaterialScreen(viewModel, screenModifier)
            AppScreen.STUDENT_STEM_GUIDE -> StemGuideScreen(viewModel, screenModifier)
            AppScreen.STUDENT_QUIZ -> QuizScreen(viewModel, screenModifier)
            AppScreen.STUDENT_GROUP_PROJECT -> GroupProjectScreen(viewModel, screenModifier)
            AppScreen.TEACHER_DASHBOARD -> TeacherDashboardScreen(viewModel, screenModifier)
        }
    }
}
