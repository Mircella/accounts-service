package kz.mircella.accounts

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AccountsConsumer {

    @KafkaListener(topics = ["accounts"], groupId = "accounts")
    fun listen(message: String) {
        println("Received account in group accounts: $message")
    }
}