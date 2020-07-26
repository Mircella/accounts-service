package kz.mircella.accounts.domain.time

import java.time.Duration
import java.time.OffsetDateTime

interface TimeProvider {

    fun now(): OffsetDateTime

    fun durationFromNow(validUntil: OffsetDateTime): Duration

    fun millisFromNow(validUntil: OffsetDateTime): Long?

    fun fromMillis(millis: Long?): OffsetDateTime
}