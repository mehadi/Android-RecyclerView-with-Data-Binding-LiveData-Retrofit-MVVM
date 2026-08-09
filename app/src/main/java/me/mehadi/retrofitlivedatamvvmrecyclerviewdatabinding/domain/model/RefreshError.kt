package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model

/**
 * Failure categories for [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository.refresh],
 * so the presentation layer can show a specific message without inspecting transport-level
 * exception types (which belong to the data layer).
 */
sealed class RefreshError(
    cause: Throwable,
) : Exception(cause) {
    class NoConnection(
        cause: Throwable,
    ) : RefreshError(cause)

    class Timeout(
        cause: Throwable,
    ) : RefreshError(cause)

    class Server(
        val code: Int,
        cause: Throwable,
    ) : RefreshError(cause)

    class Unknown(
        cause: Throwable,
    ) : RefreshError(cause)
}
