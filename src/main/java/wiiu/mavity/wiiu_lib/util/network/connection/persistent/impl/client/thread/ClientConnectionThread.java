package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.*;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadHandlerThread;

public class ClientConnectionThread extends Thread {

	protected volatile PersistentClientConnection connection;
	protected volatile String ip;
	protected volatile int port;
	protected volatile ThreadHandlerThread threadManager;

	public ClientConnectionThread(ThreadHandlerThread threadManager, String ip, int port) {
		super("ServerConnectionThread");
		this.threadManager = threadManager;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public synchronized void run() {
		this.connection = PersistentConnection.PersistentConnectionBuilder.create()
			.setTargetIp(this.ip)
			.setPort(this.port)
			.buildClient()
			.open();
		this.connection.scanInAndWriteResponseToOut();
		this.threadManager.threads.remove(this.getName());
	}
}