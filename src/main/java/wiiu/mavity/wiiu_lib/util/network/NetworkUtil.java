package wiiu.mavity.wiiu_lib.util.network;

import com.google.gson.*;

import com.mojang.util.UUIDTypeAdapter;

import wiiu.mavity.wiiu_lib.util.network.connection.*;

import java.net.*;
import java.util.*;

public class NetworkUtil {

	public static String getUsernameFromUUID(UUID uuid) {
		String content = requestResponseFromUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid));
		JsonObject json = JsonParser.parseString(content).getAsJsonObject();
		return json.get("name").getAsString();
	}

	public static String requestResponseFromUrl(String targetURL) {
		try (Connection connection = new SimpleJsonRequestConnection(url(targetURL)).open()) {
			connection.checkValidResponse();
			return connection.getResponse();
		}
	}

	public static String postJsonToUrl(String targetURL, String json, boolean mustHaveResponse) {
		try (Connection connection = new SimpleJsonPostConnection(url(targetURL), List.of(HttpResponseCode.HTTP_NO_CONTENT), mustHaveResponse).open()) {
			connection.post(json);
			connection.checkValidResponse();
			return connection.getResponse();
		}
	}

	public static URL url(String targetURL) {
		try {
			return new URL(targetURL);
		} catch (MalformedURLException e) {
			throw new NetworkException("Invalid or malformed url: '" + targetURL + "'", e);
		}
	}
}