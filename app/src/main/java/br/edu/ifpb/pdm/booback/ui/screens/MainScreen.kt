package br.edu.ifpb.pdm.booback.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.pdm.booback.DB
import br.edu.ifpb.pdm.booback.models.Book



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var books by remember { mutableStateOf(DB.getBooks()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Livros", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5)
                )
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).background(Color(0xFFE3F2FD))) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(books) { book ->
                        BookItem(book = book, onDelete = {
                            DB.removeBook(book.getId())
                            books = DB.getBooks()
                            showToast(context, "Livro removido")
                        },
                            onEdit = { }
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = book.getTitle(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            Text(text = "Autor: ${book.getAuthor()}", color = Color(0xFF0D47A1))
            Text(text = "Gênero: ${book.getGender()}", color = Color(0xFF0D47A1))
            Text(text = "Páginas: ${book.getPages()}", color = Color(0xFF0D47A1))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (book.getIsAvailable()) {
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



fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
