package wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.thread;

import wiiu.mavity.wiiu_lib.util.network.NetworkException;
import wiiu.mavity.wiiu_lib.util.network.connection.persistent.impl.client.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientThreadHandlerThread extends Thread {

	public volatile boolean running = false;
	public volatile Client client;

	public volatile Map<String, ClientConnectionThread> threads = new ConcurrentHashMap<>();

	public ClientThreadHandlerThread(Client client) {
		super("ClientThreadHandler");
		this.client = client;
	}

	@Override
	public synchronized void run() {
		this.running = true;
		System.out.println("Thread manager online.");
		do this.updateThreads(); while (this.running);
		System.out.println("All threads are stopped.");
		this.client.shouldClose = true;
	}

	public synchronized void updateThreads() {
		try {
			for (ClientConnectionThread thread : this.threads.values()) {
				if (thread.getState().equals(Thread.State.NEW)) thread.start();
			}
			if (this.threads.isEmpty()) this.running = false;
		} catch (Exception e) {
			this.running = false;
			throw new NetworkException(e);
		}
	}

	public synchronized void addThread(String ip, int port) {
		ClientConnectionThread thread = new ClientConnectionThread(this, ip, port);
		this.threads.put(thread.getName(), thread);
	}
}