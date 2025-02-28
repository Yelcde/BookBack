package br.edu.ifpb.pdm.booback.models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class UserDAO {

    private val db = FirebaseFirestore.getInstance()

    fun search(callback: (List<User>) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { document ->
                val users = document.toObjects<User>()
                callback(users)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun searchByName(name: String, callback: (User?) -> Unit) {
        db.collection("users").whereEqualTo("name", name).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val user = document.documents[0].toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


    fun searchByEmail(email: String, callback: (User?) -> Unit) {
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val user = document.documents[0].toObject<User>()
                    Log.d("UserDAO", "Usuário encontrado: $user")
                    callback(user)
                } else {
                    Log.d("UserDAO", "Nenhum usuário encontrado para o email: $email")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDAO", "Erro ao buscar usuário por email", e)
                callback(null)
            }
    }
    fun searchById(id: String, callback: (User?) -> Unit) {
        db.collection("users").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addUser(user: User, callback: (User?) -> Unit) {
        db.collection("users").add(user)
            .addOnSuccessListener { documentReference ->
                user.id = documentReference.id
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
