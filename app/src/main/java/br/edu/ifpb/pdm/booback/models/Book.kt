package br.edu.ifpb.pdm.booback.models

import com.google.firebase.firestore.DocumentId

data class Book(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var author: String = "",
    var gender: String = "",
    var pages: Int = 0,
    var isAvailable: Boolean = true
) {
}
