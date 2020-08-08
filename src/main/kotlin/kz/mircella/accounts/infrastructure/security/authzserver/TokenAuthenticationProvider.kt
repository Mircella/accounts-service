package kz.mircella.accounts.infrastructure.security.authzserver

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import java.util.ArrayList

@Component
class TokenAuthenticationProvider : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val name = authentication.name
        val password = authentication.credentials.toString()

        return  if (name == "admin" && password == "admin") {
            UsernamePasswordAuthenticationToken(name, password, ArrayList())
        } else {
            throw RuntimeException("Unknown user")
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}