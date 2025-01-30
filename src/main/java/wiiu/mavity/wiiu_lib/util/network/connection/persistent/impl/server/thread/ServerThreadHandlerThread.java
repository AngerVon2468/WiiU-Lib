package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.thread;

import wiiu.mavity.wiiu_lib.util.network.NetworkException;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.server.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThreadHandlerThread extends Thread {

	public volatile boolean running = false;
	public volatile Server server;

	public volatile Map<String, ServerConnectionThread> threads = new ConcurrentHashMap<>();

	public ServerThreadHandlerThread(Server server) {
		super("ServerThreadHandler");
		this.server = server;
	}

	@Override
	public synchronized void run() {
		this.running = true;
		System.out.println("Thread manager online.");
		do this.updateThreads(); while (this.running);
		System.out.println("All threads are stopped.");
		this.server.shouldClose = true;
	}

	public synchronized void updateThreads() {
		try {
			for (ServerConnectionThread thread : this.threads.values()) {
				if (thread.getState().equals(Thread.State.NEW)) thread.start();
			}
			if (this.threads.isEmpty()) this.running = false;
		} catch (Exception e) {
			this.running = false;
			throw new NetworkException(e);
		}
	}

	public synchronized void addThread(int port) {
		ServerConnectionThread thread = new ServerConnectionThread(this, port);
		this.threads.put(thread.getName(), thread);
	}
}