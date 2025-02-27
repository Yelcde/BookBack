package br.edu.ifpb.pdm.booback

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.edu.ifpb.pdm.booback.ui.screens.LoginScreen
import br.edu.ifpb.pdm.booback.ui.screens.MainScreen
import br.edu.ifpb.pdm.booback.ui.screens.RegisterBookScreen
import br.edu.ifpb.pdm.booback.ui.screens.RegisterScreen
import br.edu.ifpb.pdm.booback.ui.theme.BooBackTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BooBackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BooBackTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterBookScreenPreview() {
    BooBackTheme {
        RegisterBookScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BooBackTheme {
        LoginScreen(onLoginSuccess = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    BooBackTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}