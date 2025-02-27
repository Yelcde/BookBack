package br.edu.ifpb.pdm.booback

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.ifpb.pdm.booback.ui.components.BottomNavigationBar
import br.edu.ifpb.pdm.booback.ui.screens.LoginScreen
import br.edu.ifpb.pdm.booback.ui.screens.MainScreen
import br.edu.ifpb.pdm.booback.ui.screens.RegisterBookScreen
import br.edu.ifpb.pdm.booback.ui.screens.RegisterScreen
import br.edu.ifpb.pdm.booback.ui.theme.BooBackTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            BooBackApp()
        }
    }
}

@Composable
fun BooBackApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BooBackTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentRoute != "loginScreen" && currentRoute != "registerScreen") {
                    BottomNavigationBar(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "loginScreen",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("loginScreen") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("mainScreen") {
                                popUpTo("loginScreen") { inclusive = true }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate("registerScreen") {
                                popUpTo("loginScreen") { inclusive = false }
                            }
                        }
                    )
                }
                composable("mainScreen") {
                    MainScreen(navController = navController)
                }
                composable("registerBook") {
                    RegisterBookScreen(bookId = null, navController = navController)
                }
                composable("registerBook/{bookId}") { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    RegisterBookScreen(bookId = bookId, navController = navController)
                }
                composable("registerScreen") {
                    RegisterScreen(onRegisterSuccess = {
                        // Após cadastro, volte para a tela de login
                        navController.navigate("loginScreen") {
                            popUpTo("registerScreen") { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    BooBackTheme {
        MainScreen(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterBookScreenPreview() {
    val navController = rememberNavController()
    BooBackTheme {
        RegisterBookScreen(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BooBackTheme {
        LoginScreen(onLoginSuccess = {}, onRegisterClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    BooBackTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}
