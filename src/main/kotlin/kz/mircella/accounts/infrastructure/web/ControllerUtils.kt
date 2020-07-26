package kz.mircella.accounts.infrastructure.web

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionException
import java.util.concurrent.Executors
import java.util.function.Supplier

class ControllerUtils {

    companion object {

        private val executor = Executors.newSingleThreadExecutor()

        private fun <T> unwrap(future: CompletableFuture<T>): CompletableFuture<T> {
            val unwrapped = CompletableFuture<T>()
            future.thenAccept { unwrapped.complete(it) }
            future.exceptionally { ex ->
                if (ex is CompletionException) {
                    unwrapped.completeExceptionally(ex.cause)
                } else {
                    unwrapped.completeExceptionally(ex)
                }
                null
            }
            return unwrapped
        }

        fun <T> unwrappedAsync(supplier: Supplier<T>): CompletableFuture<T> {
            return unwrap(supplyAsync<T>(supplier, executor))
        }

        fun <T> doAsync(supplier: Supplier<T>): CompletableFuture<T> {
            return supplyAsync<T>(supplier, executor)
        }
    }
}
