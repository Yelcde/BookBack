package br.edu.ifpb.pdm.booback.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import br.edu.ifpb.pdm.booback.DB
import br.edu.ifpb.pdm.booback.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

// Atualizado para usar o diretório de imagens externo (conforme seu file_paths.xml)
fun createImageUri(context: Context): Uri {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile("IMG_", ".jpg", imagesDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Composable
fun RegisterBookScreen(bookId: String? = null, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var pages by remember { mutableIntStateOf(0) }
    var isAvailable by remember { mutableStateOf(false) }

    // Estados para imagem
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Usaremos essas variáveis para manter a URI que será enviada para upload
    var imageUriToUpload by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Launcher para selecionar imagem da galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        capturedImageBitmap = null
        imageUriToUpload = uri
    }

    // Launcher para capturar foto pela câmera
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    // Launcher para capturar foto pela câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempImageUri?.let { uri ->
                imageUriToUpload = uri  // garante que o URI para upload seja o mesmo
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        capturedImageBitmap = BitmapFactory.decodeStream(stream)
                        Log.d("UploadTest", "Arquivo capturado, tamanho: ${capturedImageBitmap?.byteCount} bytes")
                    } ?: Log.e("UploadTest", "Stream nulo para o URI: $uri")
                } catch (e: Exception) {
                    Log.e("UploadTest", "Erro ao ler arquivo: ${e.message}", e)
                }
            }
        } else {
            tempImageUri = null
        }
    }


    // Launcher para solicitar a permissão de CAMERA em tempo de execução
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tempImageUri = createImageUri(context)
            tempImageUri?.let { cameraLauncher.launch(it) }
        } else {
            Log.e("Permission", "Permissão de câmera negada!")
        }
    }

    // Carrega dados do livro se estiver em modo de edição
    LaunchedEffect(bookId) {
        if (bookId != null) {
            DB.getBookById(bookId) { book ->
                if (book != null) {
                    title = book.title
                    author = book.author
                    gender = book.gender
                    pages = book.pages
                    isAvailable = book.isAvailable
                    // Se o livro já tiver imagem, ela já estará salva no Firestore
                    // Caso o usuário não selecione uma nova imagem, o campo imageUrl permanecerá inalterado.
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1565C0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (bookId == null) "Cadastro de Livros" else "Editar Livro",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                // Seção de imagem
                Text(
                    text = "Imagem do Livro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (capturedImageBitmap != null) {
                    Image(
                        bitmap = capturedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Imagem Capturada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Imagem Selecionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Sem imagem")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            // Solicita a permissão de câmera se necessário
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tirar Foto")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Galeria")
                    }
                }

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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    Text("Disponível")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotEmpty() && author.isNotEmpty() && gender.isNotEmpty() && pages > 0) {
                            coroutineScope.launch {
                                // Verifica se é novo livro ou edição
                                val isNewBook = bookId.isNullOrEmpty()
                                // Se for novo, gera um ID único para o livro
                                val newBookId = if (isNewBook) {
                                    FirebaseFirestore.getInstance().collection("books").document().id
                                } else {
                                    bookId!!
                                }
                                // Cria o objeto livro sem imagem (será atualizado com a URL, se houver)
                                val book = Book(
                                    id = newBookId,
                                    title = title,
                                    author = author,
                                    gender = gender,
                                    pages = pages,
                                    isAvailable = isAvailable,
                                    imageUrl = "" // inicial; será atualizado se houver imagem
                                )

                                // Se houver uma imagem selecionada ou capturada, ela estará em imageUriToUpload
                                if (isNewBook) {
                                    if (imageUriToUpload != null) {
                                        DB.addBookWithImage(book, imageUriToUpload) { success ->
                                            if (success) navController.popBackStack()
                                            else Log.e("DB", "Erro ao adicionar livro com imagem")
                                        }
                                    } else {
                                        DB.addBook(book) { navController.popBackStack() }
                                    }
                                } else {
                                    // Atualização do livro
                                    if (imageUriToUpload != null) {
                                        DB.uploadBookImage(imageUriToUpload!!, book.id) { imageUrl ->
                                            val updatedBook = if (imageUrl != null) {
                                                book.copy(imageUrl = imageUrl)
                                            } else book
                                            DB.updateBook(updatedBook,
                                                onSuccess = { navController.popBackStack() },
                                                onFailure = { e -> Log.e("DB", "Erro ao atualizar o livro", e) }
                                            )
                                        }
                                    } else {
                                        DB.updateBook(book,
                                            onSuccess = { navController.popBackStack() },
                                            onFailure = { e -> Log.e("DB", "Erro ao atualizar o livro", e) }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (bookId.isNullOrEmpty()) "Adicionar Livro" else "Salvar Alterações",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}
