package br.edu.ifpb.pdm.booback.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.edu.ifpb.pdm.booback.DB
import br.edu.ifpb.pdm.booback.models.Book
import kotlinx.coroutines.launch


@Composable
fun RegisterBookScreen(bookId: String? = null, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var pages by remember { mutableIntStateOf(0) }
    var isAvailable by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(bookId) {
        if (bookId != null) {
            DB.getBookById(bookId) { book ->
                if (book != null) {
                    title = book.title
                    author = book.author
                    gender = book.gender
                    pages = book.pages
                    isAvailable = book.isAvailable
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (bookId == null) "Cadastro de Livros" else "Editar Livro",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF1565C0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gênero") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pages.toString(),
            onValueChange = { pages = it.toIntOrNull() ?: 0 },
            label = { Text("Total de páginas") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
            Text("Disponível")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && author.isNotEmpty() && gender.isNotEmpty() && pages > 0) {
                    coroutineScope.launch {
                        val book = Book(
                            id = bookId ?: "",
                            title = title,
                            author = author,
                            gender = gender,
                            pages = pages,
                            isAvailable = isAvailable
                        )

                        if (bookId == null) {
                            DB.addBook(book) {
                                navController.popBackStack()
                            }
                        } else {
                            DB.updateBook(book,
                                onSuccess = { navController.popBackStack() },
                                onFailure = { e -> Log.e("DB", "Erro ao atualizar o livro", e) }
                            )
                        }

                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (bookId == null) "Adicionar Livro" else "Salvar Alterações",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
