package com.example.clean.authentication

data class SignUpResult(
    val data: UserData?,
    val errorMessage: String? = null
)
