package wiiu.mavity.wiiu_lib.util.enums;

import java.util.stream.Stream;

public interface IGettableEnum<VALUE extends Enum<VALUE>, VALUE_FILTER> {

    VALUE_FILTER getFilter();

    VALUE getDefault();

    default Stream<VALUE> valuesStream() {
        return Stream.of(this.getDefault().getDeclaringClass().getEnumConstants());
    }

    default VALUE get0(VALUE_FILTER filter) {
        return valuesStream()
                .filter(obj -> this.getClass().cast(obj).getFilter().equals(filter))
                .toList()
                .stream()
                .findFirst()
                .orElse(this.getDefault());
    }
}