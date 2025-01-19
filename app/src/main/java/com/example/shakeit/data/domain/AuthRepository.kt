package com.example.shakeit.data.domain

import android.util.Patterns
import com.example.shakeit.R
import com.example.shakeit.ui.elements.MinigameData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
        onSuccess: (Int?) -> Unit,
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

                            firestore.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val avatar = document.get("avatar") as? Int
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
                        val avatar = document.get("avatar") as? Int

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onSuccess(avatar)
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

            firestore.collection("users")
                .whereEqualTo("username", newUsername)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {

                        firestore.collection("users").document(userId)
                            .update("username", newUsername)
                            .addOnSuccessListener {

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
                "Reaction Duel" to 0,
                "Shake The Bomb" to 0,
                "Maze Escape" to 0
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

    fun updateMinigameScore(
        gameName: String,
        score: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userScoreRef = firestore.collection("scores").document(userId)

            userScoreRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentScore = document.getLong(gameName) ?: 0
                        val updatedScore = currentScore + score

                        userScoreRef.update(gameName, updatedScore)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure("Error updating $gameName score: ${e.message}")
                            }
                    } else {

                        val newScoreData = mapOf(
                            gameName to score
                        )
                        userScoreRef.set(newScoreData, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure("Error adding score for $gameName: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    onFailure("Error retrieving $gameName score: ${e.message}")
                }
        } else {
            onFailure("User not logged in.")
        }
    }


    fun getLeaderboardData(onSuccess: (List<MinigameData>) -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("scores").get()
            .addOnSuccessListener { result ->
                val leaderboardData = mutableListOf<MinigameData>()
                val games = listOf("Reaction Duel", "Shake The Bomb","Maze Escape")

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

                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val data = document.data?.toMutableMap() ?: mutableMapOf()
                    data["documentId"] = document.id
                    onResult(data)
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

                    // Save user data
                    val userData = mapOf(
                        "username" to username,
                        "phoneNumber" to phoneNumber,
                        "email" to email,
                        "avatar" to null
                    )
                    firestore.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            // Initialize friends list
                            initializeUserFriends(userId, onSuccess, onFailure)
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error saving user data: ${e.message}")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Unknown error occurred")
                }
            }
    }

    fun addFriendByQr(scannedUserId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId != null && scannedUserId != currentUserId) {
            // Mutual friend
            addMutualFriend(currentUserId, scannedUserId, onSuccess, onFailure)
        } else {
            onFailure("Invalid user ID or you can't add yourself.")
        }
    }

    private fun initializeUserFriends(userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val initialFriendsData = mapOf(
            "friendsList" to emptyList<String>()
        )
        firestore.collection("friends").document(userId)
            .set(initialFriendsData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure("Error initializing friends: ${e.message}")
            }
    }

    fun addFriend(userId: String, friendId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val friendDocRef = firestore.collection("friends").document(userId)
        friendDocRef.update("friendsList", FieldValue.arrayUnion(friendId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Unknown error") }
    }

    fun addMutualFriend(userId: String, friendId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        addFriend(userId, friendId, onSuccess = {
            addFriend(friendId, userId, onSuccess, onFailure)
        }, onFailure)
    }



    fun getFriends(userId: String, onSuccess: (List<String>) -> Unit, onFailure: (String) -> Unit) {
        val friendDocRef = firestore.collection("friends").document(userId)
        friendDocRef.get()
            .addOnSuccessListener { document ->
                val friendsList = document.get("friendsList") as? List<String> ?: emptyList()
                onSuccess(friendsList)
            }
            .addOnFailureListener { onFailure(it.message ?: "Unknown error") }
    }


}
