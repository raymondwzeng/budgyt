package com.technology626.budgyt

import App
import DriverFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import createDatabase
import viewmodels.BudgetOverviewViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = BudgetOverviewViewModel(
            componentContext = defaultComponentContext(),
            database = createDatabase(DriverFactory(context = this.applicationContext))
        )


        setContent {
            App(root)
        }
    }
}