package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.Client;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadHandlerThread;

public class ClientThreadHandlerThread extends ThreadHandlerThread {

	public ClientThreadHandlerThread(Client client, boolean closeOnShutdown) {
		super(client, "Client", closeOnShutdown);
	}
}