package kz.mircella.accounts.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import kz.mircella.accounts.UserLoggedInEvent
import org.apache.kafka.common.serialization.UUIDSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.UUID

@Configuration
class KafkaProducerConfig {

    @Bean
    fun kafkaTemplate(objectMapper: ObjectMapper, kafkaProperties: KafkaProperties): KafkaTemplate<UUID, UserLoggedInEvent> {
        val properties = kafkaProperties.buildProducerProperties()
        val producerFactory: ProducerFactory<UUID, UserLoggedInEvent> =
                DefaultKafkaProducerFactory(
                        properties,
                        UUIDSerializer(),
                        JsonSerializer<UserLoggedInEvent>(objectMapper)
                )
        return KafkaTemplate(producerFactory)
    }
}