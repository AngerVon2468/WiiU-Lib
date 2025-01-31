package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client;

import wiiu.mavity.wiiu_lib.util.process.threaded.LoopableMultiThreadedProcess;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread.*;

public class Client extends LoopableMultiThreadedProcess {

	@Override
	public synchronized void createResources() {
		this.threadManager = new ClientThreadHandlerThread(this, true);
	}

	@Override
	public synchronized Client open() {
		super.open();
		return this;
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	public synchronized void addThread(String ip, int port) {
		this.addThread(new ClientConnectionThread(this.threadManager, ip, port));
	}

	public static void main(String[] args) {
		try (Client client = new Client()) {
			client.createResources();
			client.addThread("127.0.0.1", 1984);
			client.open();
			client.loop();
		}
	}
}