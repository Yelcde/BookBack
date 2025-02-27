package br.edu.ifpb.pdm.booback.models

import com.google.firebase.firestore.DocumentId

class User(
    @DocumentId var id: String = "",
    val name: String,
    val email: String,
    val password: String
) {

}