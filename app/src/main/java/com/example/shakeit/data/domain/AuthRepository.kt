package com.example.shakeit.data.domain

import android.util.Patterns
import com.example.shakeit.R
import com.example.shakeit.ui.elements.MinigameData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(
        email: String,
        password: String,
        username: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Check if email, password, and username are empty
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            onFailure("Email, password and username are mandatory!")
            return
        }

        // Check email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onFailure("Invalid email format!")
            return
        }

        // Check password length
        if (password.length < 6) {
            onFailure("Password must be at least 6 characters long!")
            return
        }

        // Check if username is already taken
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onFailure("Username is already taken!")
                } else {
                    // Check if email is already registered
                    auth.fetchSignInMethodsForEmail(email)
                        .addOnSuccessListener { result ->
                            if (!result.signInMethods.isNullOrEmpty()) {
                                onFailure("Email is already registered!")
                            } else {
                                // Create user
                                createUser(
                                    email,
                                    password,
                                    username,
                                    phoneNumber,
                                    onSuccess = {
                                        // Initialize scores
                                        initializeUserScores(
                                            username = username,
                                            onSuccess = {
                                                onSuccess()
                                            },
                                            onFailure = { error ->
                                                onFailure("Error initializing scores: $error")
                                            }
                                        )
                                    },
                                    onFailure = { error ->
                                        onFailure(error)
                                    }
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error checking email: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure("Error checking username: ${e.message}")
            }
    }

    // Function to login the user
    fun loginUser(
        usernameOrEmail: String,
        password: String,
        onSuccess: (Int?) -> Unit, // Cambia il tipo per accettare un Int (ID avatar)
        onFailure: (String) -> Unit
    ) {
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            onFailure("Email/Username and password are required.")
            return
        }

        // Check if the input is an email
        val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()

        if (isEmail) {
            // Login with email and password
            auth.signInWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            // Recupera il campo avatar
                            firestore.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val avatar = document.get("avatar") as? Int // Cambia getString con get e fai un cast
                                    onSuccess(avatar)
                                }
                                .addOnFailureListener { exception ->
                                    onFailure("Error fetching user data: ${exception.message}")
                                }
                        } else {
                            onFailure("User ID not found.")
                        }
                    } else {
                        onFailure(task.exception?.message ?: "Invalid credentials.")
                    }
                }
        } else {
            // Login with username
            firestore.collection("users")
                .whereEqualTo("username", usernameOrEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val document = querySnapshot.documents[0]
                        val email = document.getString("email") ?: ""
                        val avatar = document.get("avatar") as? Int // Cambia getString con get e fai un cast

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onSuccess(avatar) // Passa l'avatar al callback
                                } else {
                                    onFailure(task.exception?.message ?: "Invalid credentials.")
                                }
                            }
                    } else {
                        onFailure("Username not found.")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure("Error fetching username: ${exception.message}")
                }
        }
    }

    // Function to logout the user
    fun logoutUser(onSuccess: () -> Unit) {
        if (isUserLoggedIn()){
            auth.signOut()
            onSuccess()
        }
        else{
            println("User is not logged in")
        }
    }

    // Function to check if the user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }


    fun getCurrentUser(onResult: (FirebaseUser?) -> Unit) {
        val currentUser = auth.currentUser
        onResult(currentUser)
    }

    fun getUserData(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun updateUserAvatar(avatar: Int) {
        val avatarName = when (avatar) {
            R.drawable.avatar -> "avatar"
            R.drawable.avatar2 -> "avatar2"
            R.drawable.avatar3 -> "avatar3"
            else -> null
        }

        val userId = auth.currentUser?.uid
        if (userId != null && avatarName != null) {
            firestore.collection("users").document(userId)
                .update("avatar", avatarName)
                .addOnSuccessListener {
                    println("Avatar aggiornato con successo!")
                }
                .addOnFailureListener { e ->
                    println("Errore nell'aggiornamento dell'avatar: ${e.message}")
                }
        }
    }

    fun updateUsername(newUsername: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Verifica che il nuovo username non sia già in uso
            firestore.collection("users")
                .whereEqualTo("username", newUsername)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Aggiorna l'username in `users`
                        firestore.collection("users").document(userId)
                            .update("username", newUsername)
                            .addOnSuccessListener {
                                // Aggiorna l'username in `scores`
                                firestore.collection("scores").document(userId)
                                    .update("username", newUsername)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure("Error updating scores: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                onFailure("Error updating username: ${e.message}")
                            }
                    } else {
                        onFailure("Username is already taken.")
                    }
                }
                .addOnFailureListener { e ->
                    onFailure("Error checking username availability: ${e.message}")
                }
        } else {
            onFailure("User not logged in.")
        }
    }

    private fun initializeUserScores(username: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val initialScores = mapOf(
                "username" to username,
                "game1" to 0,
                "game2" to 0,
                "game3" to 0,
                "game4" to 0
            )
            firestore.collection("scores").document(userId)
                .set(initialScores)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure("Error initializing scores: ${e.message}")
                }
        } else {
            onFailure("User not logged in.")
        }
    }

    fun getLeaderboardData(onSuccess: (List<MinigameData>) -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("scores").get()
            .addOnSuccessListener { result ->
                val leaderboardData = mutableListOf<MinigameData>()
                val games = listOf("game1", "game2", "game3", "game4")

                games.forEach { game ->
                    val scores = result.documents.mapNotNull { doc ->
                        val username = doc.getString("username")
                        val score = doc.getLong(game)?.toInt()
                        if (username != null && score != null) {
                            Pair(username, score)
                        } else null
                    }.sortedByDescending { it.second }

                    leaderboardData.add(MinigameData(name = game.replaceFirstChar { it.uppercaseChar() }, scores = scores))
                }

                onSuccess(leaderboardData)
            }
            .addOnFailureListener { exception ->
                onFailure("Error fetching leaderboard: ${exception.message}")
            }
    }

    fun getUserDataByUsername(username: String, onResult: (Map<String, Any>?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(querySnapshot.documents[0].data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
    private fun createUser(
        email: String,
        password: String,
        username: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // Save user dat
                    val userData = mapOf(
                        "username" to username,
                        "phoneNumber" to phoneNumber,
                        "email" to email,
                        "avatar" to null
                    )
                    firestore.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error saving user data: ${e.message}")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Unknown error occurred")
                }
            }
    }
}
