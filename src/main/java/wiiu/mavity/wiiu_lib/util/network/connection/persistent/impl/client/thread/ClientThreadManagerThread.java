package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.Client;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadManagerThread;

public class ClientThreadManagerThread extends ThreadManagerThread {

	public ClientThreadManagerThread(Client client, boolean closeOnShutdown) {
		super(client, "Client", closeOnShutdown);
	}
}