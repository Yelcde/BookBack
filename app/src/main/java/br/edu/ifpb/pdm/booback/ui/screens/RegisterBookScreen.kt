package br.edu.ifpb.pdm.booback.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.pdm.booback.DB
import br.edu.ifpb.pdm.booback.models.Book

@Composable
fun RegisterBookScreen() {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var pages by remember { mutableIntStateOf(0) }
    var isAvailable by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Cadastro de Livros", style = MaterialTheme.typography.headlineMedium)

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
            onValueChange = { pages = it.toInt() },
            label = { Text("Total de páginas") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isAvailable,
                onCheckedChange = { isAvailable = it }
            )
            Text(
                text = "Disponível",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && author.isNotEmpty() && gender.isNotEmpty()) {
                    DB.addBook(
                        Book(
                            title = title,
                            author = author,
                            gender = gender,
                            pages = pages,
                            isAvailable = isAvailable
                        )
                    )
                    title = ""
                    author = ""
                    gender = ""
                    pages = 0
                    isAvailable = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF91CAFF) // Azul #91CAFF
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Adicionar Livro", fontSize = 18.sp)
        }
    }
}