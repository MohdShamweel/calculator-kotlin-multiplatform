package com.jetbrains.kmm.shared.manager

import com.jetbrains.kmm.shared.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        if (Firebase.auth.currentUser != null) {
            _user.value = Firebase.auth.currentUser?.toUser()
        }
    }

    suspend fun login(idToken: String, accessToken: String? = null, onSuccess: () -> Unit) {
        val firebaseCredential = GoogleAuthProvider.credential(idToken, accessToken)
        try {
            val authResult = Firebase.auth.signInWithCredential(firebaseCredential)
            authResult.user?.let {
                _user.value = it.toUser()
            }
            onSuccess()
        } catch (e: Exception) {
            // Handle sign-in failure
            // You might want to throw this exception or handle it in some way
            throw e
        }
    }

    suspend fun logout() {
        Firebase.auth.signOut()
    }

    fun isLoggedIn() = Firebase.auth.currentUser != null

    private fun FirebaseUser.toUser() = User(
        id = uid,
        name = displayName,
        email = email,
        photoUrl = photoURL
    )
}