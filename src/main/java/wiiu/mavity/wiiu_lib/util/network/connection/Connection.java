package wiiu.mavity.wiiu_lib.util.network.connection;

import wiiu.mavity.wiiu_lib.util.ObjectHolder;
import wiiu.mavity.wiiu_lib.util.network.*;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class Connection implements AutoCloseable {

    protected final ObjectHolder<HttpURLConnection> connection = new ObjectHolder<>();
    protected final ObjectHolder<String> response = new ObjectHolder<>();
    protected final URL url;
	protected final Map<String, String> requestProperties;
	protected final boolean
            doOutput,
            mustHaveResponse,
            useCaches;
    protected boolean open = false;
	protected final Charset responseCharset;
	protected final List<HttpResponseCode> acceptableResponseCodes;
	protected final RequestMethod requestMethod;

    protected Connection(
            URL url,
            Map<String, String> requestProperties,
            boolean doOutput,
            boolean mustHaveResponse,
            boolean useCaches,
            Charset responseCharset,
            List<HttpResponseCode> acceptableResponseCodes,
            RequestMethod requestMethod
    ) {
        this.url = url;
        this.requestProperties = requestProperties;
        this.doOutput = doOutput;
        this.mustHaveResponse = mustHaveResponse;
        this.useCaches = useCaches;
        this.responseCharset = responseCharset;
        this.acceptableResponseCodes = acceptableResponseCodes;
        this.requestMethod = requestMethod;
    }

    protected Connection(ConnectionBuilder builder) {
        this(
                builder.url,
                builder.requestProperties,
                builder.doOutput,
                builder.mustHaveResponse,
                builder.useCaches,
                builder.responseCharset,
                builder.acceptableResponseCodes,
                builder.requestMethod
        );
    }

    public URL getUrl() {
        return this.url;
    }

    public Map<String, String> getRequestProperties() {
        return Collections.unmodifiableMap(this.requestProperties);
    }

    public boolean getDoOutput() {
        return this.doOutput;
    }

    public boolean getMustHaveResponse() {
        return this.mustHaveResponse;
    }

    public boolean getUseCaches() {
        return this.useCaches;
    }

    public Charset getResponseCharset() {
        return this.responseCharset;
    }

    public List<HttpResponseCode> getAcceptableResponseCodes() {
        return Collections.unmodifiableList(this.acceptableResponseCodes);
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public boolean isOpen() {
        return this.open;
    }

    public int getRawResponseCode() {
        return this.connection.ifPresentOrElse(
                (connection) -> {
                    try {
                        return this.isOpen() ? connection.getResponseCode() : -1;
                    } catch (Exception e) {
                        throw new NetworkException(this.getUrl(), e);
                    }
                },
                () -> -1
        );
    }

    public HttpResponseCode getResponseCode() {
        return HttpResponseCode.get(this.getRawResponseCode());
    }

    private void setRequestProperties() {
        this.connection.ifPresent(connection -> this.getRequestProperties().forEach(connection::setRequestProperty));
    }

    private void setProperties() {
        this.connection.ifPresent(connection -> {
            try {
                connection.setRequestMethod(this.getRequestMethod().getRequestMethod());
                connection.setDoOutput(this.getDoOutput());
                connection.setUseCaches(this.getUseCaches());
            } catch (Exception e) {
                throw new NetworkException(this.getUrl(), e);
            }
        });
    }

    public String getResponse() {
        return this.response.computeIfAbsent(() -> {
            try {
                if (!this.getMustHaveResponse()) return "No response required.";
                this.checkOpen();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                connection.getOrThrow("Connection must be open to get response!").getInputStream(),
                                this.getResponseCharset()
                        )
                )) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) response.append(responseLine.trim());
                    return response.toString();
                }
            } catch (Exception e) {
                throw new NetworkException(this.getUrl(), e);
            }
        });
    }

    public void checkValidResponse() {
        HttpResponseCode responseCode = this.getResponseCode();
        if (!this.getAcceptableResponseCodes().contains(responseCode)) {
            throw new NetworkException(
                    this.getUrl(),
                    responseCode,
                    this.getAcceptableResponseCodes()
            );
        }
    }

    public void checkOpen() {
        if (!this.isOpen()) throw new NetworkException("Connection must be open!");
    }

    public void post(String toPost) {
        if (!this.getDoOutput()) throw new NetworkException("Connection must support output to post!");
        this.checkOpen();
        this.connection.ifPresent(connection -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(toPost);
                writer.close();
            } catch (IOException e) {
                throw new NetworkException(this.getUrl(), e);
            }
        });
    }

    public Connection open() {
        try {
            this.connection.forceSet(this.getUrl().openConnection());
            this.setRequestProperties();
            this.setProperties();
            this.open = true;
        } catch (Exception e) {
            this.open = false;
            throw new NetworkException(this.getUrl(), e);
        }
        return this;
    }

    @Override
    public void close() {
        this.checkValidResponse();
        this.getResponse();
        this.connection.ifPresent(HttpURLConnection::disconnect);
        this.open = false;
    }

    public static class ConnectionBuilder {

        private URL url = NetworkUtil.url("https://www.example.com");
        private final Map<String, String> requestProperties = new HashMap<>();
        private boolean
                doOutput = false,
                mustHaveResponse = false,
                useCaches = true;
        private Charset responseCharset = Charset.defaultCharset();
        private final List<HttpResponseCode> acceptableResponseCodes = new ArrayList<>(List.of(HttpResponseCode.HTTP_OK));
        private RequestMethod requestMethod = RequestMethod.GET;

        private ConnectionBuilder() {}

        public static ConnectionBuilder create() {
            return new ConnectionBuilder();
        }

        public ConnectionBuilder setURL(URL url) {
            this.url = url;
            return this;
        }

        public ConnectionBuilder setURL(String url) {
            this.url = NetworkUtil.url(url);
            return this;
        }

        public ConnectionBuilder setRequestProperties(Map<String, String> requestProperties) {
            this.resetRequestProperties();
            return this.addRequestProperties(requestProperties);
        }

        public ConnectionBuilder setRequestProperty(String key, String value) {
            this.requestProperties.put(key, value);
            return this;
        }

        public ConnectionBuilder removeRequestProperty(String key) {
            this.requestProperties.remove(key);
            return this;
        }

        public ConnectionBuilder addRequestProperties(Map<String, String> requestProperties) {
            this.requestProperties.putAll(requestProperties);
            return this;
        }

        public ConnectionBuilder removeRequestProperties(String... keys) {
            for (String key : keys) this.requestProperties.remove(key);
            return this;
        }

        public ConnectionBuilder resetRequestProperties() {
            this.requestProperties.clear();
            return this;
        }

        public ConnectionBuilder setAcceptableResponseCodes(List<HttpResponseCode> acceptableResponseCodes) {
            this.clearAcceptableResponseCodes();
            return this.addAcceptableResponseCodes(acceptableResponseCodes);
        }

        public ConnectionBuilder addAcceptableResponseCode(HttpResponseCode acceptableResponseCode) {
            this.acceptableResponseCodes.add(acceptableResponseCode);
            return this;
        }

        public ConnectionBuilder removeAcceptableResponseCode(HttpResponseCode acceptableResponseCode) {
            this.acceptableResponseCodes.remove(acceptableResponseCode);
            return this;
        }

        public ConnectionBuilder addAcceptableResponseCodes(List<HttpResponseCode> acceptableResponseCodes) {
            this.acceptableResponseCodes.addAll(acceptableResponseCodes);
            return this;
        }

        public ConnectionBuilder removeAcceptableResponseCodes(List<HttpResponseCode> acceptableResponseCodes) {
            this.acceptableResponseCodes.removeAll(acceptableResponseCodes);
            return this;
        }

        public ConnectionBuilder clearAcceptableResponseCodes() {
            this.acceptableResponseCodes.clear();
            return this;
        }

        public ConnectionBuilder setDoOutput(boolean doOutput) {
            this.doOutput = doOutput;
            return this;
        }

        public ConnectionBuilder enableOutput() {
            return this.setDoOutput(true);
        }

        public ConnectionBuilder disableOutput() {
            return this.setDoOutput(false);
        }

        public ConnectionBuilder setMustHaveResponse(boolean mustHaveResponse) {
            this.mustHaveResponse = mustHaveResponse;
            return this;
        }

        public ConnectionBuilder requireResponse() {
            return this.setMustHaveResponse(true);
        }

        public ConnectionBuilder doNotRequireResponse() {
            return this.setMustHaveResponse(false);
        }

        public ConnectionBuilder setUseCaches(boolean useCaches) {
            this.useCaches = useCaches;
            return this;
        }

        public ConnectionBuilder enableCaching() {
            return this.setUseCaches(true);
        }

        public ConnectionBuilder disableCaching() {
            return this.setUseCaches(false);
        }

        public ConnectionBuilder setResponseCharset(Charset responseCharset) {
            this.responseCharset = responseCharset;
            return this;
        }

        public ConnectionBuilder enableJson() {
            return this.setJson(true);
        }

        public ConnectionBuilder disableJson() {
            return this.setJson(false);
        }

        public ConnectionBuilder setJson(boolean json) {
            String contentType = "Content-Type";
            String accept = "Accept";
            String applicationJson = "application/json";
            if (!json) {
                this.removeRequestProperty(contentType);
                this.removeRequestProperty(accept);
            } else {
                this.setRequestProperty(contentType, applicationJson);
                this.setRequestProperty(accept, applicationJson);
            }
            return this;
        }

        public ConnectionBuilder setRequestMethod(RequestMethod requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public ConnectionBuilder setRequestMethod(String requestMethod) {
            return this.setRequestMethod(RequestMethod.get(requestMethod));
        }

        public Connection build() {
            return new Connection(this);
        }
    }
}