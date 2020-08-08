package kz.mircella.accounts.infrastructure.web.api

import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails

@RestController
class RestUserController{
    @RequestMapping("/api/users/me")
    fun profile(): ResponseEntity<UserProfile>? { //Build some dummy data to return for testing
//        val user = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val email: String = "user" + "@howtodoinjava.com"
        val profile = UserProfile("user", email)
        return ResponseEntity.ok<UserProfile>(profile)
    }
}

data class UserProfile(val name: String, val email: String)