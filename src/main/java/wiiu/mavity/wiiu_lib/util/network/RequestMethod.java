package wiiu.mavity.wiiu_lib.util.network;

import wiiu.mavity.wiiu_lib.util.enums.IGettableStringEnum;

public enum RequestMethod implements IGettableStringEnum<RequestMethod> {

    UNKNOWN("UNKNOWN"),
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE");

    private final String requestMethod;

    RequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    @Override
    public String getFilter() {
        return this.getRequestMethod();
    }

    @Override
    public RequestMethod getDefault() {
        return UNKNOWN;
    }

    public static RequestMethod get(String requestMethod) {
        return UNKNOWN.get0(requestMethod);
    }
}