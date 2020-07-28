package kz.mircella.accounts.infrastructure.security.authzserver.pkce

import org.springframework.security.oauth2.common.exceptions.InvalidRequestException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.regex.Pattern

object PkceVerifier {
    private const val METHOD_PLAIN = "plain"
    private const val METHOD_S256 = "S256"
    private const val MIN_CODE_VERIFIER_LENGTH = 43
    private const val MAX_CODE_VERIFIER_LENGTH = 128
    private const val CODE_CHALLENGE_LENGTH = 43
    private val REGEX_CODE_VERIFIER = Pattern.compile("^[0-9a-zA-Z\\-._~]{43,128}$")

    fun validatePkceAuthentication(pkceAuthentication: PkceAuthentication){
        val verifier: String = validateCodeVerifier(pkceAuthentication.codeVerifier)
        val challengeMethod: String = pkceAuthentication.codeChallengeMethod
        if (getEncodedVerifier(verifier, challengeMethod) != validateCodeChallenge(pkceAuthentication.codeChallenge)) {
            throw InvalidRequestException("Invalid PKCE codeVerifier")
        }
    }

    private fun validateCodeChallenge(codeChallenge: String): String {
        if (codeChallenge.length != CODE_CHALLENGE_LENGTH) {
            throw InvalidRequestException(String.format("Code challenge length must be %d characters", CODE_CHALLENGE_LENGTH))
        }
        return codeChallenge
    }

    private fun validateCodeVerifier(codeVerifier: String): String {
        val length = codeVerifier.length
        if (length < MIN_CODE_VERIFIER_LENGTH || length > MAX_CODE_VERIFIER_LENGTH) {
            throw InvalidRequestException(String.format("Code verifier length must be between %d and %d characters",
                    MIN_CODE_VERIFIER_LENGTH,
                    MAX_CODE_VERIFIER_LENGTH))
        }
        if (!REGEX_CODE_VERIFIER.matcher(codeVerifier).matches()) {
            throw InvalidRequestException(
                    "Code verifier contains illegal characters")
        }
        return codeVerifier
    }
    private fun getEncodedVerifier(verifier: String, method: String): String {
        return when (method) {
            METHOD_S256 -> encodeCodeVerifier(verifier)
            METHOD_PLAIN -> verifier
            else -> throw InvalidRequestException("Invalid code challenge method")
        }
    }

    /**
     * code challenge used in oauth2 should be (SHA256(ASCII(code_verifier)) and encoded with BASE64URL-ENCODE and no padding
     */
    private fun encodeCodeVerifier(codeVerifier: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val codeVerifierBytes = messageDigest.digest(codeVerifier.toByteArray(StandardCharsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes)
    }
}