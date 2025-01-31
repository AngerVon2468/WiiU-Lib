package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.*;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.*;

public class ClientConnectionThread extends HandledThread {

	protected volatile PersistentClientConnection connection;
	protected volatile String ip;
	protected volatile int port;

	public ClientConnectionThread(ThreadManagerThread threadManager, String ip, int port) {
		super(threadManager, "ClientConnectionThread");
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
		super.run();
	}
}