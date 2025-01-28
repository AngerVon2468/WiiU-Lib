package wiiu.mavity.wiiu_lib.util;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IfPresentConsumer<T> {

	default void acceptOrDoNothing(@Nullable T t) {
		acceptOrElse(t, null);
	}

	default void acceptOrElse(@Nullable T t, @Nullable EmptyFunctionalInterface emptyConsumer) {
		if (t != null) accept(t);
		else if (emptyConsumer != null) emptyConsumer.function();
	}

	void accept(T t);
}