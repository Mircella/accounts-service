//package kz.mircella.accounts.infrastructure.security.authorizationcode
//
//import kz.mircella.accounts.infrastructure.security.authzserver.pkce.PkceAuthentication
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.oauth2.common.util.RandomValueStringGenerator
//import org.springframework.security.oauth2.provider.ClientDetailsService
//import org.springframework.security.oauth2.provider.OAuth2Authentication
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
//import java.util.concurrent.ConcurrentHashMap
//
//class CustomAuthorizationCodeServices(private val clientDetailsService: ClientDetailsService) : AuthorizationCodeServices {
//
//    private val generator = RandomValueStringGenerator()
//    private val authorizationCodeStore: ConcurrentHashMap<String, PkceAuthentication> = ConcurrentHashMap()
//
//    override fun createAuthorizationCode(authentication: OAuth2Authentication): String {
//        val pkceAuthentication = PkceAuthentication.from(authentication)
//        val clientSecret: String = clientDetailsService.loadClientByClientId(pkceAuthentication.clientId).clientSecret
//        println(clientSecret)
//        val code = generator.generate()
//        authorizationCodeStore[code] = pkceAuthentication
//        return code
//
//    }
//
//    override fun consumeAuthorizationCode(code: String): OAuth2Authentication {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}