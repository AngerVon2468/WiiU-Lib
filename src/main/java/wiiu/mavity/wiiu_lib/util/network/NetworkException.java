package wiiu.mavity.wiiu_lib.util.network;

import java.net.URL;
import java.util.*;

public class NetworkException extends RuntimeException {

	public static String errorRequest(String url) {
		return "An error occurred during a request to url '" + url + "'";
	}

	public static String errorRequest(URL url) {
		return errorRequest(url.toString());
	}

	public static String badResponse(URL url, HttpResponseCode actualResponse, HttpResponseCode... expectedResponses) {
		return badResponse(url, actualResponse, List.of(expectedResponses));
	}

	public static String badResponse(URL url, HttpResponseCode actualResponse, List<HttpResponseCode> expectedResponses) {
		if (expectedResponses.isEmpty()) return "You're supposed to call this method with at least one expected response code!";
		String msg = errorRequest(url) + ", expected response code(s): ";
		StringJoiner sj = new StringJoiner(", or ");
		expectedResponses.stream().map(HttpResponseCode::toString).forEach(sj::add);
		msg += sj + "; Got response: " + actualResponse;
		return msg;
	}

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(URL url, HttpResponseCode actualResponse, HttpResponseCode... expectedResponses) {
		this(badResponse(url, actualResponse, expectedResponses));
	}

	public NetworkException(URL url, HttpResponseCode actualResponse, List<HttpResponseCode> expectedResponses) {
		this(badResponse(url, actualResponse, expectedResponses));
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}

	public NetworkException(URL url) {
		this(errorRequest(url));
	}

	public NetworkException(URL url, Throwable cause) {
		this(errorRequest(url), cause);
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}
}