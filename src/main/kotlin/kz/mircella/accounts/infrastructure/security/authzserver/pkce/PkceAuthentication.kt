package kz.mircella.accounts.infrastructure.security.authzserver.pkce

import org.springframework.security.oauth2.common.exceptions.InvalidRequestException
import org.springframework.security.oauth2.provider.OAuth2Authentication

data class PkceAuthentication private constructor(
        val codeVerifier: String, val codeChallenge: String, val codeChallengeMethod: String,
        val clientId: String, val authentication: OAuth2Authentication
) {

    companion object {
        fun from(authentication: OAuth2Authentication): PkceAuthentication {
            val requestParameters = authentication.oAuth2Request.requestParameters
            val clientId = requiredParam(requestParameters, "client_id")
            val codeVerifier = requiredParam(requestParameters, "code_verifier")
            val codeChallenge = requiredParam(requestParameters, "code_challenge")
            val codeChallengeMethod = requiredParam(requestParameters, "code_challenge_method")
            return PkceAuthentication(codeVerifier, codeChallenge, codeChallengeMethod, clientId, authentication)
        }

        private fun requiredParam(requestParameters: MutableMap<String, String>, name: String) =
                requestParameters[name] ?: throw InvalidRequestException(
                        "Parameter '$name' is required when using PKCE")

    }
}