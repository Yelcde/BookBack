package br.edu.ifpb.pdm.booback.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import br.edu.ifpb.pdm.booback.models.Recommendation
import br.edu.ifpb.pdm.booback.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// json-server --watch db.json --port 5000


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var recommendation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Carrega a lista de livros do Firestore
    LaunchedEffect(Unit) {
        DB.getBooks(
            onSuccess = { books = it },
            onFailure = { e -> Log.e("Firebase", "Erro ao buscar livros", e) }
        )
    }


    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getRecommendation().enqueue(object : Callback<List<Recommendation>> {
            override fun onResponse(
                call: Call<List<Recommendation>>,
                response: Response<List<Recommendation>>
            ) {
                if (response.isSuccessful) {
                    val recommendations = response.body()
                    // Caso o JSON possua o campo "texto" e o seu modelo esteja usando "text", ajuste aqui
                    recommendation = recommendations?.random()?.texto ?: "Por enquanto, aproveite seus livros"
                } else {
                    error = "Erro ao carregar recomendações"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Recommendation>>, t: Throwable) {
                error = "Falha na conexão: ${t.message}"
                isLoading = false
            }
        })
    }

    // Exemplo: Mostra a recomendação na tela (você pode ajustar conforme seu design)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Livros", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E88E5)),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFE3F2FD))
            ) {
                // Exibe a recomendação, se disponível
                if (isLoading) {
                    Text(
                        text = "Buscando contos...",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    if (error.isNotEmpty()) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = recommendation,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
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
        Row(modifier = Modifier.padding(16.dp)) {
            // Se houver imagem, exibe-a em uma caixa fixa
            if (!book.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sem imagem", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Coluna com os detalhes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    fontSize = 18.sp,
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Disponível", color = Color(0xFF4CAF50))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Indisponível",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Indisponível", color = Color.Red)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
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
