package wiiu.mavity.wiiu_lib.util;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.*;
import java.util.function.*;

public class JsonUtils {

	public static <K, V> void saveJsonMap(JsonWriter writer, String jsonObjectName, Map<K, V> map, boolean close) {
		try {
			writer.name(jsonObjectName).beginObject();
			for (Map.Entry<K, V> entrySet : map.entrySet()) {
				String key = String.valueOf(entrySet.getKey());
				V value = entrySet.getValue();
				if (value instanceof Collection<?> valuesList) JsonUtils.saveJsonList(writer, key, valuesList, false);
				else {
					writer.name(key);
					writer.beginObject();
					writer.value(String.valueOf(value));
					writer.endObject();
				}
			}
			writer.endObject();
			if (close) writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveJsonList(JsonWriter writer, String jsonObjectName, Iterable<?> list, boolean close) {
		JsonUtils.advancedSaveJsonList(
			writer,
			jsonObjectName,
			String::valueOf,
			list,
			close
		);
	}

	public static <K, V extends Iterable<?>> void advancedSaveJsonMapHoldingList(
		JsonWriter writer,
		String jsonObjectName,
		Map<K, V> map,
		Function<K, String> keyToString,
		Function<V, String[]> valueToString,
		boolean close
	) {
		try {
			writer.name(jsonObjectName).beginObject();
			for (Map.Entry<K, V> entrySet : map.entrySet()) {
				writer.name(keyToString.apply(entrySet.getKey()));
				writer.beginArray();
				for (String value : valueToString.apply(entrySet.getValue())) writer.value(value);
				writer.endArray();
			}
			writer.endObject();
			if (close) writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <K, V> void advancedSaveJsonMap(
		JsonWriter writer,
		String jsonObjectName,
		Map<K, V> map,
		Function<K, String> keyToString,
		Function<V, String> valueToString,
		boolean close
	) {
		try {
			writer.name(jsonObjectName).beginObject();
			for (Map.Entry<K, V> entrySet : map.entrySet()) {
				writer.name(keyToString.apply(entrySet.getKey()));
				if (entrySet.getValue() instanceof Collection<?>) throw new RuntimeException("Called advanceSaveJsonMap expecting a map holding object values but was provided a map holding a Collection (abstraction of List)!");
				else {
					writer.beginObject();
					writer.value(valueToString.apply(entrySet.getValue()));
					writer.endObject();
				}
			}
			writer.endObject();
			if (close) writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <V> void advancedSaveJsonList(
		JsonWriter writer,
		String jsonObjectName,
		Function<V, String> valueToString,
		Iterable<V> list,
		boolean close
	) {
		JsonUtils.advancedSaveJsonList(
			writer,
			() -> jsonObjectName,
			valueToString,
			list,
			close
		);
	}

	public static <V> void advancedSaveJsonList(
		JsonWriter writer,
		Supplier<String> jsonObjectNameSupplier,
		Function<V, String> valueToString,
		Iterable<V> list,
		boolean close
	) {
		try {
			writer.name(jsonObjectNameSupplier.get()).beginArray();
			for (V value : list) writer.value(valueToString.apply(value));
			writer.endArray();
			if (close) writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}