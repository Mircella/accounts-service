package kz.mircella.accounts.infrastructure.security.authzserver.jwt

import com.nimbusds.jose.jwk.RSAKey
import org.bouncycastle.jcajce.provider.digest.SHA3
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3
import org.bouncycastle.util.encoders.Hex
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.jwt.codec.Codecs
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.validation.constraints.NotNull

@Service
@EnableConfigurationProperties(KeyProperties::class)
class JwtJwksKeyService(private val keyProperties: KeyProperties) {

    fun generateKeyPair(): KeyPair {
        val privatePKCS8Key = PKCS8EncodedKeySpec(Codecs.b64Decode(getStartEndSectionLessPrivateKey(keyProperties.privateKey)))
        val publicX509Key = X509EncodedKeySpec(Codecs.b64Decode(getStartEndSectionLessPublicKey(keyProperties.publicKey)))
        val factory = keyFactory
        return try {
            KeyPair(factory.generatePublic(publicX509Key), factory.generatePrivate(privatePKCS8Key))
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    fun encodeToSha3(key: @NotNull String?): String {
        val digest: DigestSHA3 = SHA3.Digest512()
        return Hex.toHexString(digest.digest(key!!.toByteArray()))
    }

    fun getStartEndSectionLessPublicKey(publicKey: String): ByteArray {
        return getStartEndSectionKey(publicKey, PEM_PUBLIC_START_SECTION, PEM_PUBLIC_END_SECTION)
    }

    fun getStartEndSectionLessPrivateKey(privateKey: String): ByteArray {
        return getStartEndSectionKey(privateKey, PEM_PRIVATE_START_SECTION, PEM_PRIVATE_END_SECTION)
    }

    private val keyFactory: KeyFactory = try {
        KeyFactory.getInstance(RSA_ALG)
    } catch (e: Exception) {
        throw IllegalArgumentException(e)
    }

    private fun getRsaKey(factory: KeyFactory, key: JwtJwksPublicKey): RSAKey {
        val publicX509Key = X509EncodedKeySpec(Codecs.b64Decode(getStartEndSectionLessPublicKey(key.publicKey)))
        return try {
            val publicKey = factory.generatePublic(publicX509Key) as RSAPublicKey
            RSAKey.Builder(publicKey).build()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    private fun getStartEndSectionKey(key: String, startSection: String, endSection: String): ByteArray {
        return key.replace(startSection.toRegex(), EMPTY_CHARACTER).replace(endSection.toRegex(), EMPTY_CHARACTER).toByteArray()
    }

    companion object {
        const val PEM_PRIVATE_START_SECTION = "-----BEGIN PRIVATE KEY-----"
        const val PEM_PRIVATE_END_SECTION = "-----END PRIVATE KEY-----"
        const val PEM_PUBLIC_START_SECTION = "-----BEGIN PUBLIC KEY-----"
        const val PEM_PUBLIC_END_SECTION = "-----END PUBLIC KEY-----"
        private const val EMPTY_CHARACTER = ""
        private const val RSA_ALG = "RSA"
    }
}
