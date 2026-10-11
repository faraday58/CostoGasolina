package com.mexiti.costogasolina.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mexiti.costogasolina.room.GasolineDao
import com.mexiti.costogasolina.ui.views.AddView
import com.mexiti.costogasolina.ui.views.HomeView
import com.mexiti.costogasolina.viewmodel.GasolineViewModel

@Composable
fun NavManager(
    dao: GasolineDao,
){
    val navController = rememberNavController()

    // Factory para instanciar el ViewModel pasando el DAO
    val viewModel: GasolineViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                GasolineViewModel(dao)
            }
        }
    )



    NavHost(
        navController = navController,
        startDestination = "Home",
    ){
        composable("Home"){
            HomeView(navController,viewModel)
        }
        composable("AddView"){
            AddView(navController, viewModel)
        }
    }
}