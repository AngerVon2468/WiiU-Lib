package wiiu.mavity.wiiu_lib.util.network.connection;

import wiiu.mavity.wiiu_lib.util.network.*;

import java.net.URL;
import java.util.List;

public class SimpleJsonPostConnection extends Connection {

    public SimpleJsonPostConnection(
            URL url,
            List<HttpResponseCode> acceptableResponseCodes,
            boolean mustHaveResponse
    ) {
        super(
                ConnectionBuilder.create()
                        .setURL(url)
                        .addAcceptableResponseCodes(acceptableResponseCodes)
                        .setMustHaveResponse(mustHaveResponse)
                        .enableOutput()
                        .enableJson()
                        .setRequestMethod(RequestMethod.POST)
        );
    }

    @Override
    public SimpleJsonPostConnection open() {
        super.open();
        return this;
    }
}