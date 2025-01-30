package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.*;

public class ServerConnectionThread extends Thread {

	protected volatile PersistentServerConnection connection;
	protected volatile int port;
	protected volatile ServerThreadHandlerThread threadManager;

	public ServerConnectionThread(ServerThreadHandlerThread threadManager, int port) {
		super("ServerConnectionThread-" + System.currentTimeMillis());
		this.threadManager = threadManager;
		this.port = port;
	}

	@Override
	public synchronized void run() {
		this.connection = PersistentConnection.PersistentConnectionBuilder.create()
			.setPort(this.port)
			.buildServer()
			.open();
		this.connection.scanInAndWriteResponseToOut();
		this.threadManager.threads.remove(this.getName());
	}
}