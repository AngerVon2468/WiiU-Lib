package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl;

import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.thread.server.ServerThreadHandlerThread;

public class Server implements AutoCloseable {

	public volatile ServerThreadHandlerThread thread;
	public volatile boolean shouldClose = false;

	public static void main(String[] args) {
		try (Server server = new Server().run()) {
			server.thread.addThread(6666);
			server.thread.addThread(1984);
			server.loop();
		}
	}

	public void loop() {
		do this.preventShutdownByLooping(); while (!this.shouldClose);
	}

	private void preventShutdownByLooping() {}

	public Server() {
		this.thread = new ServerThreadHandlerThread(this);
	}

	public synchronized Server run() {
		this.thread.start();
		return this;
	}

	@Override
	public synchronized void close() {
		if (!this.shouldClose) this.shouldClose = true;
		if (this.thread.getState().equals(Thread.State.RUNNABLE)) this.thread.stop();
	}
}