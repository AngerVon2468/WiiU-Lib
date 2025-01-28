package wiiu.mavity.wiiu_lib.util.enums;

public interface IGettableStringEnum<V extends Enum<V>> extends IGettableEnum<V, String> {

    String getFilter();

    V getDefault();
}