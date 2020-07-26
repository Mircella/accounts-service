package kz.mircella.accounts.infrastructure.time

import kz.mircella.accounts.domain.time.TimeProvider
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
class SystemTimeProvider : TimeProvider {

    override fun now(): OffsetDateTime {
        return OffsetDateTime.now()
    }

    override fun durationFromNow(validUntil: OffsetDateTime): Duration {
        return durationBetween(now(), validUntil)
    }

    override fun millisFromNow(validUntil: OffsetDateTime): Long? {
        return millisBetween(now(), validUntil)
    }

    override fun fromMillis(millis: Long?): OffsetDateTime {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis!!), ZoneId.systemDefault())
    }

    companion object {

        internal fun durationBetween(from: OffsetDateTime, to: OffsetDateTime): Duration {
            return Duration.between(from, to)
        }

        internal fun millisBetween(from: OffsetDateTime, to: OffsetDateTime): Long {
            return durationBetween(from, to).toMillis()
        }
    }
}