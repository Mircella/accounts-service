package kz.mircella.accounts.infrastructure.security.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService : UserDetailsService {
    private val authUserRepository = AuthUserRepository()
    private val authGroupRepository = AuthGroupRepository()

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = authUserRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("cannot find username: $username")
        val authGroups = authGroupRepository.findByUsername(username)
        return UserPrincipal(user, authGroups)
    }
}