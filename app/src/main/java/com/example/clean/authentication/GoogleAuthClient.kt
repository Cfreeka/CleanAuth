package com.example.clean.authentication

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.clean.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    private val context: Context
) {

    private val webClient = BuildConfig.WEB_CLIENT
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithCredentialManager(): SignUpResult {
        return try {
            val googleOption = GetGoogleIdOption.Builder()
                .setServerClientId(webClient)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .build()
            val passwordOption = GetPasswordOption()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .addCredentialOption(passwordOption)
                .build()

            val credentialResponse: GetCredentialResponse =
                credentialManager.getCredential(context, request)
            val credential = credentialResponse.credential

            authenticateWithFirebase(credential)


        } catch (e: NoCredentialException) {
            SignUpResult(null, "No credential exception")
        } catch (e: GetCredentialException) {
            SignUpResult(null, "Get credential exception")
        }
    }

    private suspend fun authenticateWithFirebase(credential: Credential): SignUpResult {
        return try {


            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            if (googleIdToken.isEmpty()) {
                SignUpResult(null, " No google idToken")
            }

            val firebaseAuth = GoogleAuthProvider.getCredential(googleIdToken, null)
            val user = auth.signInWithCredential(firebaseAuth).await().user

            SignUpResult(
                data = user?.run {
                    UserData(
                        email = email,
                        userName = displayName,
                        photoUrl = photoUrl.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignUpResult(null, "Error authenticating")
        }

    }

    suspend fun signInWithPasswordAndEmail(email: String, password: String): SignUpResult {

        return try {


            val user = auth.signInWithEmailAndPassword(email, password).await().user
            SignUpResult(
                data = user?.run {
                    UserData(
                        email = email,
                        userName = displayName ?: "No name",
                        photoUrl = photoUrl.toString() ?: ""
                    )
                },
                errorMessage = null
            )
        } catch (e: NoCredentialException) {
            SignUpResult(null, " No credentials")

        }
    }
    suspend fun signUpWithPasswordAndEmail(email: String, password: String): SignUpResult {

        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            SignUpResult(
                data = user?.run {
                    UserData(
                        email = email,
                        userName = displayName ?: "No name",
                        photoUrl = photoUrl.toString() ?: ""
                    )
                },
                errorMessage = null
            )
        } catch (e: NoCredentialException) {
            SignUpResult(null, " No credentials")

        }
    }

    fun getCurrentUser(): UserData? = auth.currentUser?.run {
        UserData(
            userName = displayName,
            email = email,
            photoUrl = photoUrl.toString()
        )
    }

    suspend fun signOut() {
        try {
            val request = ClearCredentialStateRequest()
            auth.signOut()
            credentialManager.clearCredentialState(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}