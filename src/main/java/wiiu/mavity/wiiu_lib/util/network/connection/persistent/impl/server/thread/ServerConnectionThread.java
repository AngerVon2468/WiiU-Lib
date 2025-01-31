package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.*;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadHandlerThread;

public class ServerConnectionThread extends Thread {

	protected volatile PersistentServerConnection connection;
	protected volatile int port;
	protected volatile ThreadHandlerThread threadManager;

	public ServerConnectionThread(ThreadHandlerThread threadManager, int port) {
		super("ServerConnectionThread");
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