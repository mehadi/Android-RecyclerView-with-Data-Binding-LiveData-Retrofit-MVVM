package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.util;

/**
 * A generic class that holds a value or an exception.
 * Used for handling success and error states in a type-safe way (2025 best practice)
 *
 * @param <T> The type of data
 */
public sealed class Result<T> permits Result.Success, Result.Error, Result.Loading {
    
    private Result() {}

    public static final class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class Error<T> extends Result<T> {
        private final Throwable exception;
        private final String message;

        public Error(Throwable exception) {
            this.exception = exception;
            this.message = exception.getMessage();
        }

        public Error(String message) {
            this.exception = null;
            this.message = message;
        }

        public Throwable getException() {
            return exception;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class Loading<T> extends Result<T> {
        public Loading() {}
    }
}

