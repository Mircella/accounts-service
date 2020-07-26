package kz.mircella.accounts.infrastructure.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper().registerModules(JavaTimeModule(), Jdk8Module())
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(OffsetDateTime::class.java, object : JsonSerializer<OffsetDateTime>() {
            @Throws(IOException::class, JsonProcessingException::class)
            override fun serialize(offsetDateTime: OffsetDateTime, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
                jsonGenerator.writeString(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(offsetDateTime))
            }
        })
        objectMapper.registerModule(simpleModule)
        return objectMapper
    }
}