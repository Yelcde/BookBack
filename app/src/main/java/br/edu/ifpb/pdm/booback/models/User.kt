package br.edu.ifpb.pdm.booback.models

import com.google.firebase.firestore.DocumentId

class User() {

    @DocumentId
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var password: String = ""

    constructor(id: String, name: String, email: String, password: String) : this() {
        this.id = id
        this.name = name
        this.email = email
        this.password = password
    }
}