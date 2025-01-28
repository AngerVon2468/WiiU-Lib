package wiiu.mavity.wiiu_lib.util.network;

import wiiu.mavity.wiiu_lib.util.enums.IGettableIntegerEnum;

import java.net.HttpURLConnection;

/**
 * Enum wrapper for HTTP response codes
 */
public enum HttpResponseCode implements IGettableIntegerEnum<HttpResponseCode> {

	UNKNOWN(-1),
	HTTP_OK(HttpURLConnection.HTTP_OK),
	HTTP_CREATED(HttpURLConnection.HTTP_CREATED),
	HTTP_ACCEPTED(HttpURLConnection.HTTP_ACCEPTED),
	HTTP_NOT_AUTHORITATIVE(HttpURLConnection.HTTP_NOT_AUTHORITATIVE),
	HTTP_NO_CONTENT(HttpURLConnection.HTTP_NO_CONTENT),
	HTTP_RESET(HttpURLConnection.HTTP_RESET),
	HTTP_PARTIAL(HttpURLConnection.HTTP_PARTIAL),
	HTTP_MULTIPLE_CHOICES(HttpURLConnection.HTTP_MULT_CHOICE),
	HTTP_MOVED_PERMANENTLY(HttpURLConnection.HTTP_MOVED_PERM),
	HTTP_MOVED_TEMPORARILY(HttpURLConnection.HTTP_MOVED_TEMP),
	HTTP_SEE_OTHER(HttpURLConnection.HTTP_SEE_OTHER),
	HTTP_NOT_MODIFIED(HttpURLConnection.HTTP_NOT_MODIFIED),
	HTTP_USE_PROXY(HttpURLConnection.HTTP_USE_PROXY),
	HTTP_BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST),
	HTTP_UNAUTHORIZED(HttpURLConnection.HTTP_UNAUTHORIZED),
	HTTP_PAYMENT_REQUIRED(HttpURLConnection.HTTP_PAYMENT_REQUIRED),
	HTTP_FORBIDDEN(HttpURLConnection.HTTP_FORBIDDEN),
	HTTP_NOT_FOUND(HttpURLConnection.HTTP_NOT_FOUND),
	HTTP_BAD_METHOD(HttpURLConnection.HTTP_BAD_METHOD),
	HTTP_NOT_ACCEPTABLE(HttpURLConnection.HTTP_NOT_ACCEPTABLE),
	HTTP_PROXY_AUTH(HttpURLConnection.HTTP_PROXY_AUTH),
	HTTP_CLIENT_TIMEOUT(HttpURLConnection.HTTP_CLIENT_TIMEOUT),
	HTTP_CONFLICT(HttpURLConnection.HTTP_CONFLICT),
	HTTP_GONE(HttpURLConnection.HTTP_GONE),
	HTTP_LENGTH_REQUIRED(HttpURLConnection.HTTP_LENGTH_REQUIRED),
	HTTP_PRECONDITION_FAILED(HttpURLConnection.HTTP_PRECON_FAILED),
	HTTP_ENTITY_TOO_LARGE(HttpURLConnection.HTTP_ENTITY_TOO_LARGE),
	HTTP_REQUEST_URI_TOO_LONG(HttpURLConnection.HTTP_REQ_TOO_LONG),
	HTTP_UNSUPPORTED_MEDIA_TYPE(HttpURLConnection.HTTP_UNSUPPORTED_TYPE),
	HTTP_INTERNAL_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR),
	HTTP_NOT_IMPLEMENTED(HttpURLConnection.HTTP_NOT_IMPLEMENTED),
	HTTP_BAD_GATEWAY(HttpURLConnection.HTTP_BAD_GATEWAY),
	HTTP_SERVICE_UNAVAILABLE(HttpURLConnection.HTTP_UNAVAILABLE),
	HTTP_GATEWAY_TIMEOUT(HttpURLConnection.HTTP_GATEWAY_TIMEOUT),
	HTTP_VERSION_NOT_SUPPORTED(HttpURLConnection.HTTP_VERSION);

	private final int code;

	HttpResponseCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return this.name() + " (" + this.getCode() + ")";
	}

	@Override
	public Integer getFilter() {
		return this.getCode();
	}

	@Override
	public HttpResponseCode getDefault() {
		return UNKNOWN;
	}

	public static HttpResponseCode get(int code) {
		return UNKNOWN.get0(code);
	}
}