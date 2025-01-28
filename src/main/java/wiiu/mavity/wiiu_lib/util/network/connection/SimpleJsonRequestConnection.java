package wiiu.mavity.wiiu_lib.util.network.connection;

import wiiu.mavity.wiiu_lib.util.network.*;

import java.net.URL;
import java.util.List;

public class SimpleJsonRequestConnection extends Connection {

    public SimpleJsonRequestConnection(URL url, List<HttpResponseCode> acceptableResponseCodes) {
        super(
                ConnectionBuilder.create()
                        .setURL(url)
                        .addAcceptableResponseCodes(acceptableResponseCodes)
                        .enableJson()
                        .requireResponse()
                        .setRequestMethod(RequestMethod.GET)
        );
    }

    public SimpleJsonRequestConnection(URL url) {
        this(url, List.of());
    }

    @Override
    public SimpleJsonRequestConnection open() {
        super.open();
        return this;
    }
}