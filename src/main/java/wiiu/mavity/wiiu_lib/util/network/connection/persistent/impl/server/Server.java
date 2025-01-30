package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server;

import wiiu.mavity.wiiu_lib.OpenableAutoCloseable;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread.ServerThreadHandlerThread;

public abstract class Server implements OpenableAutoCloseable<Server> {

	public volatile ServerThreadHandlerThread thread;
	public volatile boolean shouldClose = false;

	public Server() {
		this.thread = new ServerThreadHandlerThread(this);
	}

	public synchronized void loop() {
		do this.loop0(); while (!this.shouldClose);
	}

	protected synchronized void loop0() {}

	public synchronized Server open() {
		this.thread.start();
		return this;
	}

	@Override
	public synchronized void close() {
		if (!this.shouldClose) this.shouldClose = true;
		if (this.thread.getState().equals(Thread.State.RUNNABLE)) this.thread.stop();
	}

	public static void main(String[] args) {
		try (Server server = new Server() {}) {
			server.open();
			server.thread.addThread(6666);
			server.thread.addThread(1984);
			server.loop();
		}
	}
}