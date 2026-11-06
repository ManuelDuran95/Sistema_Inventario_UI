package com.example.sistemainventario

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleSignInCancelado : Exception("Inicio con Google cancelado")

object GoogleSignInHelper {
    suspend fun obtenerIdToken(context: Context): String {
        val webClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = CredentialManager.create(context)
                .getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                throw IllegalStateException("No se recibió un token de Google válido")
            }
        } catch (e: GetCredentialCancellationException) {
            throw GoogleSignInCancelado()
        } catch (e: NoCredentialException) {
            throw IllegalStateException("No hay cuentas de Google en este dispositivo")
        }
    }

    suspend fun limpiarCredenciales(context: Context) {
        try {
            CredentialManager.create(context)
                .clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        } catch (_: Exception) {
        }
    }
}
