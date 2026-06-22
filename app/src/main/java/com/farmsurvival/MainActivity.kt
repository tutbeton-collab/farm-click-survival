package com.farmsurvival

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.farmsurvival.data.SharedPrefGameRepository
import com.farmsurvival.ui.navigation.FarmNavHost
import com.farmsurvival.ui.theme.FarmSurvivalTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: SharedPrefGameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = SharedPrefGameRepository(applicationContext)

        setContent {
            FarmSurvivalTheme {
                FarmNavHost(repository = repository)
            }
        }
    }
}
