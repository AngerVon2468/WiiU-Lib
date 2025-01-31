package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.Server;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadHandlerThread;

public class ServerThreadHandlerThread extends ThreadHandlerThread {

	public ServerThreadHandlerThread(Server client, boolean closeOnShutdown) {
		super(client, "Server", closeOnShutdown);
	}
}