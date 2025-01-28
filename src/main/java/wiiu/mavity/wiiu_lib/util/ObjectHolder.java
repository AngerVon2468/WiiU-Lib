package wiiu.mavity.wiiu_lib.util;

import org.jetbrains.annotations.ApiStatus.*;
import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.function.*;

/**
 * A holder class to contain potentially null values of a given object type, with a few extra features for convenience and utility.
 * @param <V> The type of the value.
 */
public class ObjectHolder<V> implements ObjectHolderLike<V> {

	/**
	 * The value contained by this holder.
	 */
    private @Nullable V value;

	/**
	 * Creates a new holder with the initial value set to the one specified in the parameters.
	 * @param initialValue The initial value to set.
	 */
    public ObjectHolder(@Nullable V initialValue) {
        this.set(initialValue);
    }

	/**
	 * Creates a new holder with a null value.
	 */
    public ObjectHolder() {
        this.set(null);
    }

	/**
	 * @return The value contained in this holder.
	 */
	@Override
    public @Nullable V get() {
        return this.value;
    }

	/**
	 * Sets the value contained in this holder to the one specified in the parameters.
	 * @param newValue The new value to set.
	 */
	@Override
    public void set(@Nullable V newValue) {
        this.value = newValue;
    }

	/**
	 * Sets the value contained in this holder to the one contained in the holder specified in the parameters.
	 * @param other The holder to copy the value from.
	 * @throws NullPointerException If the other holder is null.
	 */
	public void setFrom(ObjectHolderLike<V> other) {
		Objects.requireNonNull(other);
		this.set(other.get());
	}

	/**
	 * For usage with {@link ObjectHolderLike} objects.
	 */
	@Override
	public ObjectHolder<V> getHolder() {
		return this;
	}

	/**
	 * @param defaultValue The value to return if the value contained in this holder is null.
	 * @return The value contained in this holder as a non-null value, or the default value if the value is null.
	 */
	public V getOrDefault(@NotNull V defaultValue) {
        return this.isPresent() ? this.get() : defaultValue;
    }

	/**
	 * @return The value contained in this holder as a non-null value, or throws an exception if the value is null.
	 * @throws NullPointerException If the value contained in this object is null.
	 */
	public @NotNull V getOrThrow() throws NullPointerException {
		return this.getOrThrow("Value was null!");
	}

	/**
	 * @return The value contained in this holder as a non-null value, or throws an exception if the value is null.
	 * @param message The message to add to the exception if the value is null.
	 * @throws NullPointerException If the value contained in this object is null.
	 */
	public @NotNull V getOrThrow(String message) throws NullPointerException {
		return Objects.requireNonNull(this.get(), message);
	}

	/**
	 * If this holder is empty, sets the value contained in this holder to the value returned by the supplier specified in the parameters.
	 * @param supplier The supplier to get the value from.
	 * @return The value contained in this holder (asserted non-null).
	 */
	public @NotNull V computeIfAbsent(Supplier<V> supplier) {
		Objects.requireNonNull(supplier);
		if (this.isEmpty()) this.set(supplier.get());
		return this.getOrThrow("Error not possible, value cannot be null at this point.");
	}

	/**
	 * If this holder is present, sets the value contained in this holder to the value returned by the function specified in the parameters, passing in the value contained in this holder.
	 * @param function The function to get the value from.
	 * @return The value contained in this holder.
	 */
	public @Nullable V computeIfPresent(Function<V, V> function) {
		Objects.requireNonNull(function);
		if (this.isPresent()) this.set(function.apply(this.get()));
		return this.get();
	}

	/**
	 * @return A string representation of the value contained in this holder, see {@link String#valueOf(Object)} for formatting.
	 */
    public String getAsString() {
        return String.valueOf(this.get());
    }

	/**
	 * @return A string representation of the value contained in this holder, formatted for JSON usage.
	 */
	public String getAsJsonString() {
		return this.ifPresentOrElse((value) -> {
			if (!(value instanceof Boolean) && !(value instanceof Number)) return "\"" + value + "\"";
			else return String.valueOf(value);
		}, () -> "null");
	}

	/**
	 * Forcibly casts an object to the type of the value contained in this holder.
	 * @implNote This method is here for internal use only, DO NOT USE THIS METHOD WHATSOEVER.
	 */
	@SuppressWarnings("unchecked")
	@Experimental
	@Internal
	@NonExtendable
	@VisibleForTesting
	public final void forceSet(Object o) {
		this.set((V) o);
	}

	/**
	 * Casts the value contained in this holder to the type specified in the parameters, DO NOT USE IF YOU ARE NOT CERTAIN THE CAST IS SAFE.
	 * @param clazz Class to cast this holder's value to.
	 * @return The value contained in this holder cast as the type specified in the parameters, or null if the value is null.
	 */
	public <O> @Nullable O cast(Class<O> clazz) {
		return this.isPresent() ? clazz.cast(this.get()) : null;
	}

	/**
	 * @return If the value contained in this holder is present (is not null).
	 */
    public boolean isPresent() {
        return !this.isEmpty();
    }

	/**
	 * @return If the value contained in this holder is not present (is null).
	 */
	public boolean isEmpty() {
		return this.get() == null;
	}

	/**
	 * Executes a consumer if the value contained in this holder is present (is not null).
	 * @param consumer The consumer to execute.
	 */
    public void ifPresent(@NotNull IfPresentConsumer<V> consumer) {
        consumer.acceptOrDoNothing(this.get());
    }

	/**
	 * Executes a consumer if the value contained in this holder is present (is not null), otherwise executes an empty function.
	 * @param consumer The consumer to execute.
	 * @param emptyFunction The empty function to execute.
	 */
    public void ifPresentOrElse(@NotNull IfPresentConsumer<V> consumer, @NotNull EmptyFunctionalInterface emptyFunction) {
        consumer.acceptOrElse(this.get(), emptyFunction);
    }

	/**
	 * Executes a function if the value contained in this holder is present (is not null), otherwise executes an empty function.
	 * @param function The function to execute.
	 * @param emptyFunction The empty function to execute.
	 */
    public <R> R ifPresentOrElse(@NotNull IfPresentFunction<V, R> function, @NotNull AdaptiveEmptyFunctionalInterface<R> emptyFunction) {
		return function.applyOrElse(this.get(), emptyFunction);
    }

	/**
	 * @return The class of the value contained in this holder, or {@link Void#TYPE} if the value is null.
	 */
	public Class<?> getType() {
		return this.ifPresentOrElse(V::getClass, () -> Void.TYPE);
	}

	/**
	 * @param obj The object to check against.
	 * @return Whether the object is a holder of the same type, and
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof ObjectHolder<?> other)) return false;
		return this.getType() == other.getType() && (this.getType() != Void.TYPE && other.getType() != Void.TYPE);
	}

	/**
	 * @param obj The object to check against.
	 * @return If the following conditions are met:<br>
	 * 1. If the other object is also an ObjectHolder, and<br>
	 * 2. Both holders have a present and equal value to each other; or<br>
	 * 3. Both holders are empty, and their values are both null.
	 */
	public boolean deepEquals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof ObjectHolder<?> other)) return false;
		if (this.getType() != other.getType()) return false;
		if (this.isPresent() && other.isPresent()) return this.getOrThrow().equals(other.getOrThrow());
        return this.isEmpty() && other.isEmpty() && Objects.equals(this.get(), other.get());
    }

	/**
	 * @return a string representation of the object, formatted as '{@code ObjectHolder@hashCode{value=value}}'.
	 */
	@Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "{value=" + this.getAsString() + "}";
    }
}