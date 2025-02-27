package br.edu.ifpb.pdm.booback.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF1E88E5)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)   },
            label = { Text("Home", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("MainScreen") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Cadastrar Livro", tint = Color.White) },
            label = { Text("Cadastrar", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("registerBook") }
        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White) },
//            label = { Text("Perfil") },
//            selected = false,
//            onClick = { navController.navigate("profile") }
//        )
    }
}
