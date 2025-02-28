package br.edu.ifpb.pdm.booback

import android.annotation.SuppressLint
import android.util.Log
import br.edu.ifpb.pdm.booback.models.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.auth.FirebaseAuth

object DB {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("books")

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun addBook(book: Book, onComplete: (Boolean) -> Unit) {
        booksCollection.add(book)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateBook(book: Book, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (book.id.isEmpty()) {

            onFailure(Exception("bookId não fornecido"))
            return
        }

        val bookRef = booksCollection.document(book.id)
        bookRef.set(book)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun removeBook(bookId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        booksCollection.document(bookId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getBooks(onSuccess: (List<Book>) -> Unit, onFailure: (Exception) -> Unit) {
        booksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                onFailure(exception)
                return@addSnapshotListener
            }

            val booksList = snapshot?.documents?.mapNotNull { it.toObject<Book>() } ?: emptyList()
            onSuccess(booksList)
        }
    }

    fun logout() {
        auth.signOut()
    }


    fun getBookById(bookId: String, callback: (Book?) -> Unit) {
        booksCollection.document(bookId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val book = document.toObject(Book::class.java)
                    callback(book)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookDB", "Erro ao buscar livro por ID", e)
                callback(null)
            }
    }
}
