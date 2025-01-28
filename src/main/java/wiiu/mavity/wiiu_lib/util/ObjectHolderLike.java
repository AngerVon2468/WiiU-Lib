package wiiu.mavity.wiiu_lib.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ObjectHolderLike<V> extends Supplier<V> {

	@Override
	@Nullable
	V get();

	default void set(V value) {} // Override has functionality

	default ObjectHolder<V> getHolder() {
		return new ObjectHolder<>(this.get());
	}
}