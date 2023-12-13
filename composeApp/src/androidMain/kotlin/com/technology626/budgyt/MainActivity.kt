package com.technology626.budgyt

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import viewmodels.BudgetOverviewViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = BudgetOverviewViewModel(
            componentContext = defaultComponentContext()
        )

        setContent {
            App(root)
        }
    }
}