package br.edu.ifpb.pdm.booback.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import br.edu.ifpb.pdm.booback.DB
import br.edu.ifpb.pdm.booback.models.Book
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        DB.getBooks(
            onSuccess = { books = it },
            onFailure = { e -> Log.e("Firebase", "Erro ao buscar livros", e) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Livros", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5)
                ),
                actions = {
                    TextButton(onClick = {
                        DB.logout()
                        navController.navigate("loginScreen") {
                            popUpTo("mainScreen") { inclusive = true }
                        }
                    }) {
                        Text("Sair", color = Color.White)
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFE3F2FD))
            ) {
                items(books) { book ->
                    BookItem(
                        book = book,
                        onDelete = {
                            coroutineScope.launch {
                                DB.removeBook(
                                    book.id,
                                    onSuccess = { Log.d("BookItem", "Livro removido com sucesso") },
                                    onFailure = { Log.e("BookItem", "Erro ao remover livro") }
                                )
                            }
                        },
                        onEdit = { navController.navigate("registerBook/${book.id}") }
                    )
                }
            }
        }
    )
}

@Composable
fun BookItem(book: Book, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Exibe a imagem do livro se existir
            if (!book.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Conteúdo do card com os detalhes do livro
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = book.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
                Text(text = "Autor: ${book.author}", color = Color(0xFF0D47A1))
                Text(text = "Gênero: ${book.gender}", color = Color(0xFF0D47A1))
                Text(text = "Páginas: ${book.pages}", color = Color(0xFF0D47A1))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (book.isAvailable) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Disponível",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Disponível", color = Color(0xFF4CAF50))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Indisponível",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Indisponível", color = Color.Red)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                    ) {
                        Text("Editar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remover")
                    }
                }
            }
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
