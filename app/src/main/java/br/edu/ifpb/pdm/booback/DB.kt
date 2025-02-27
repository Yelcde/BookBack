package br.edu.ifpb.pdm.booback

import br.edu.ifpb.pdm.booback.models.Book
import java.util.UUID

class DB private constructor() {
    companion object {
        private val books = mutableListOf<Book>()

        init {
            books.add(
                Book(
                    id = UUID.randomUUID(),
                    title = "Dom Casmurro",
                    author = "Machado de Assis",
                    gender = "Romance",
                    pages = 256,
                    isAvailable = true
                )
            )
            books.add(
                Book(
                    id = UUID.randomUUID(),
                    title = "1984",
                    author = "George Orwell",
                    gender = "Ficção Distópica",
                    pages = 328,
                    isAvailable = false
                )
            )
            books.add(
                Book(
                    id = UUID.randomUUID(),
                    title = "O Pequeno Príncipe",
                    author = "Antoine de Saint-Exupéry",
                    gender = "Infantil",
                    pages = 96,
                    isAvailable = true
                )
            )
        }

        fun addBook(book: Book) {
            books.add(book)
        }

        fun getBooks(): List<Book> {
            return books.toList()
        }

        fun findBookById(id: UUID): Book? {
            return books.find { it.getId() == id }
        }

        fun updateBook(id: UUID, newBook: Book): Boolean {
            val index = books.indexOfFirst { it.getId() == id }
            return if (index != -1) {
                books[index] = newBook.copy(id = id)
                true
            } else {
                false
            }
        }

        fun removeBook(id: UUID): Boolean {
            return books.removeIf { it.getId() == id }
        }
    }
}
