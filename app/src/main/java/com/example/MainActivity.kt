package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui.AuthScreen
import com.example.ui.CatalogScreen
import com.example.ui.PlannerScreen
import com.example.ui.ProfileScreen
import com.example.ui.ProgressScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: YogaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                YogaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun YogaApp(viewModel: YogaViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar"),
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple("Planner", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
                        Triple("200 Poses", Icons.Filled.SelfImprovement, Icons.Outlined.SelfImprovement),
                        Triple("Progress", Icons.Filled.PhotoCamera, Icons.Outlined.PhotoCamera),
                        Triple("Profile", Icons.Filled.Person, Icons.Outlined.Person)
                    )

                    items.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                        val isSelected = selectedNavIndex == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedNavIndex = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) filledIcon else outlinedIcon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF15803D),
                                selectedTextColor = Color(0xFF15803D),
                                indicatorColor = Color(0xFFDCFCE7),
                                unselectedIconColor = Color(0xFF6B7280),
                                unselectedTextColor = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.testTag("nav_item_${label.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedNavIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "NavTransition"
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> PlannerScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                    1 -> CatalogScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                    2 -> ProgressScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                    3 -> ProfileScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
