package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client;

import wiiu.mavity.wiiu_lib.OpenableAutoCloseable;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread.ClientThreadHandlerThread;

public class Client implements OpenableAutoCloseable<Client> {

	public volatile ClientThreadHandlerThread thread;
	public volatile boolean shouldClose = false;

	public Client() {
		this.thread = new ClientThreadHandlerThread(this);
	}

	public synchronized void loop() {
		do this.loop0(); while (!this.shouldClose);
	}

	protected synchronized void loop0() {}

	public synchronized Client open() {
		this.thread.start();
		return this;
	}

	@Override
	public synchronized void close() {
		if (!this.shouldClose) this.shouldClose = true;
		if (this.thread.getState().equals(Thread.State.RUNNABLE)) this.thread.stop();
	}

	public static void main(String[] args) {
		try (Client client = new Client().open()) {
			// don't commit ip
			client.loop();
		}
	}
}