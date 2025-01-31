package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server;

import wiiu.mavity.wiiu_lib.util.process.threaded.LoopableMultiThreadedProcess;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread.*;

public class Server extends LoopableMultiThreadedProcess {

	@Override
	public synchronized void createResources() {
		this.threadManager = new ServerThreadManagerThread(this, true);
	}

	@Override
	public synchronized Server open() {
		super.open();
		return this;
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	public synchronized void addThread(int port) {
		this.addThread(new ServerConnectionThread(this.threadManager, port));
	}

	public static void main(String[] args) {
		try (Server server = new Server()) {
			server.createResources();
			server.addThread(1984);
			server.open();
			server.loop();
		}
	}
}