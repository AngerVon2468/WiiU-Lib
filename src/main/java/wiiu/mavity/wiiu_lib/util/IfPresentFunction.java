package wiiu.mavity.wiiu_lib.util;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IfPresentFunction<T, R> {

	default R applyOrElse(@Nullable T t, AdaptiveEmptyFunctionalInterface<R> emptyConsumer) {
		if (t != null) return this.apply(t);
		else return emptyConsumer.function();
	}

	R apply(T t);
}