package wiiu.mavity.wiiu_lib.util.enums;

public interface IGettableIntegerEnum<V extends Enum<V>> extends IGettableEnum<V, Integer> {

    Integer getFilter();

    V getDefault();
}