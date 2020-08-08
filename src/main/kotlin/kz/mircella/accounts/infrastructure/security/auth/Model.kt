package kz.mircella.accounts.infrastructure.security.auth

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class AuthUser(val id: UUID = UUID.randomUUID(), val username: String, val password: String)

data class AuthGroup(val id: UUID = UUID.randomUUID(), val username: String, val authGroup: String)

data class UserPrincipal(val authUser: AuthUser, val authGroups: List<AuthGroup>) : UserDetails {

    override fun getAuthorities(): Collection<SimpleGrantedAuthority> {
        return authGroups.map { SimpleGrantedAuthority(it.authGroup) }
    }

    override fun getPassword(): String? {
        return this.authUser.password
    }

    override fun getUsername(): String? {
        return this.authUser.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
