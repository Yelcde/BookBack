package br.edu.ifpb.pdm.booback.models

class Book(
    private val title: String,
    private val author: String,
    private val gender: String,
    private val pages: Int,
    private val isAvailable: Boolean
) {
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
}