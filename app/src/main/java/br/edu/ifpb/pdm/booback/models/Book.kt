package br.edu.ifpb.pdm.booback.models

import java.util.UUID

class Book(
    private val id: UUID = UUID.randomUUID(),
    private val title: String,
    private val author: String,
    private val gender: String,
    private val pages: Int,
    private val isAvailable: Boolean
) {
    fun getId(): UUID {
        return this.id
    }

    fun getTitle(): String {
        return this.title
    }

    fun getAuthor(): String {
        return this.author
    }

    fun getGender(): String {
        return this.gender
    }

    fun getPages(): Int {
        return this.pages
    }

    fun getIsAvailable(): Boolean {
        return this.isAvailable
    }

    fun copy(
        id: UUID = this.id,
        title: String = this.title,
        author: String = this.author,
        gender: String = this.gender,
        pages: Int = this.pages,
        isAvailable: Boolean = this.isAvailable
    ): Book {
        return Book(id, title, author, gender, pages, isAvailable)
    }
}