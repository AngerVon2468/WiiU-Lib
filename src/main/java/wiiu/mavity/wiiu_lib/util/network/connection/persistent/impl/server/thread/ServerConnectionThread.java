package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.*;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.*;

public class ServerConnectionThread extends HandledThread {

	protected volatile PersistentServerConnection connection;
	protected volatile int port;

	public ServerConnectionThread(ThreadManagerThread threadManager, int port) {
		super(threadManager, "ServerConnectionThread");
		this.port = port;
	}

	@Override
	public synchronized void run() {
		this.connection = PersistentConnection.PersistentConnectionBuilder.create()
			.setPort(this.port)
			.buildServer()
			.open();
		this.connection.scanInAndWriteResponseToOut();
		super.run();
	}
}