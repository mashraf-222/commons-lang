/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang3.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A functional interface like {@link Predicate} that declares a {@link Throwable}.
 *
 * @param <T> Predicate type.
 * @param <E> The kind of thrown exception or error.
 * @since 3.11
 */
@FunctionalInterface
public interface FailablePredicate<T, E extends Throwable> {

    /** FALSE singleton */
    @SuppressWarnings("rawtypes")
    FailablePredicate FALSE = t -> false;

    /** TRUE singleton */
    @SuppressWarnings("rawtypes")
    FailablePredicate TRUE = t -> true;

    /**
     * Gets the FALSE singleton.
     *
     * @param <T> Predicate type.
     * @param <E> The kind of thrown exception or error.
     * @return The NOP singleton.
     */
    @SuppressWarnings("unchecked")
    static <T, E extends Throwable> FailablePredicate<T, E> falsePredicate() {
        return FALSE;
    }

    /**
     * Gets the TRUE singleton.
     *
     * @param <T> Predicate type.
     * @param <E> The kind of thrown exception or error.
     * @return The NOP singleton.
     */
    @SuppressWarnings("unchecked")
    static <T, E extends Throwable> FailablePredicate<T, E> truePredicate() {
        return TRUE;
    }

    /**
     * Returns a composed {@link FailablePredicate} like {@link Predicate#and(Predicate)}.
     *
     * @param other a predicate that will be logically-ANDed with this predicate.
     * @return a composed {@link FailablePredicate} like {@link Predicate#and(Predicate)}.
     * @throws NullPointerException if other is null
     */
    default FailablePredicate<T, E> and(final FailablePredicate<? super T, E> other) {
        if (other == null) {
            throw new NullPointerException();
        }

        // Fast-path: if 'this' is the canonical TRUE, the result is just 'other'.
        if (this == TRUE) {
            @SuppressWarnings("unchecked")
            final FailablePredicate<T, E> cast = (FailablePredicate<T, E>) other;
            return cast;
        }

        // Fast-path: if 'this' is the canonical FALSE, the result is canonical FALSE.
        if (this == FALSE) {
            @SuppressWarnings("unchecked")
            final FailablePredicate<T, E> castFalse = (FailablePredicate<T, E>) FALSE;
            return castFalse;
        }

        // If 'other' is canonical TRUE, the result is this.
        if (other == TRUE) {
            @SuppressWarnings("unchecked")
            final FailablePredicate<T, E> cast = (FailablePredicate<T, E>) this;
            return cast;
        }

        // If 'other' is canonical FALSE, the result is canonical FALSE.
        if (other == FALSE) {
            @SuppressWarnings("unchecked")
            final FailablePredicate<T, E> castFalse = (FailablePredicate<T, E>) FALSE;
            return castFalse;
        }

        // If both are the same reference, return this to avoid allocating a new lambda.
        if (this == other) {
            @SuppressWarnings("unchecked")
            final FailablePredicate<T, E> cast = (FailablePredicate<T, E>) this;
            return cast;
        }

        // Default composed predicate with short-circuit semantics.
        return t -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that negates this predicate.
     *
     * @return a predicate that negates this predicate.
     */
    default FailablePredicate<T, E> negate() {
        return t -> !test(t);
    }

    /**
     * Returns a composed {@link FailablePredicate} like {@link Predicate#and(Predicate)}.
     *
     * @param other a predicate that will be logically-ORed with this predicate.
     * @return a composed {@link FailablePredicate} like {@link Predicate#and(Predicate)}.
     * @throws NullPointerException if other is null
     */
    default FailablePredicate<T, E> or(final FailablePredicate<? super T, E> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    /**
     * Tests the predicate.
     *
     * @param object the object to test the predicate on
     * @return the predicate's evaluation
     * @throws E if the predicate fails
     */
    boolean test(T object) throws E;
}
