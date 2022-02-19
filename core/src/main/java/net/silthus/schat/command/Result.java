/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.command;

import java.util.Optional;

/**
 * Represents a generic result, which can either be successful or fail.
 *
 * @since 1.0.0-alpha.4
 */
@FunctionalInterface
public interface Result {

    /**
     * Instance of {@link Result} which always reports success.
     *
     * @since 1.0.0-alpha.4
     */
    Result GENERIC_SUCCESS = () -> true;

    /**
     * Instance of {@link Result} which always reports failure.
     *
     * @since 1.0.0-alpha.4
     */
    Result GENERIC_FAILURE = () -> false;

    static Result success() {
        return GENERIC_SUCCESS;
    }

    static Result failure() {
        return GENERIC_FAILURE;
    }

    static Result error(Throwable exception) {
        return new ResultImpl(false, exception);
    }

    static Result of(boolean result) {
        return new ResultImpl(result, null);
    }

    /**
     * Gets if the operation which produced this result completed successfully.
     *
     * @return if the result indicates a success
     * @since 1.0.0-alpha.4
     */
    boolean wasSuccessful();

    default boolean wasFailure() {
        return !wasSuccessful();
    }

    /**
     * The exception that lead to the failed result.
     *
     * @return the exception responsible for the result failure
     * @since 1.0.0-alpha.4
     */
    default Optional<Throwable> failureReason() {
        return Optional.empty();
    }

    /**
     * Raises an {@link Error} if the result {@link #wasFailure()} and contains a {@link #failureReason()}.
     *
     * @return the result if no error occured
     * @throws Error the error encapsulating the underlying {@link #failureReason()}
     */
    default Result raiseError() throws Error {
        final Throwable throwable = failureReason().orElse(null);
        if (throwable != null)
            throw new Error(throwable);
        return this;
    }

    class Error extends RuntimeException {
        public Error(Throwable cause) {
            super(cause);
        }
    }
}
