package wiiu.mavity.wiiu_lib.util.process.threaded.thread;

import wiiu.mavity.wiiu_lib.annotation.EffectivelyFinal;
import wiiu.mavity.wiiu_lib.util.process.threaded.LoopableMultiThreadedProcess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class ThreadManagerThread extends Thread {

	@EffectivelyFinal
	public volatile ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<>();
	public volatile boolean running = false;
	@EffectivelyFinal
	public volatile LoopableMultiThreadedProcess process;
	@EffectivelyFinal
	public volatile String name;
	@EffectivelyFinal
	public volatile boolean closeProcessOnShutdown;

	public volatile Predicate<ThreadManagerThread> runningPredicate = thread -> !thread.threads.isEmpty();

	public final synchronized void setPredicate(Predicate<ThreadManagerThread> predicate) {
		this.runningPredicate = predicate;
	}

	public ThreadManagerThread(LoopableMultiThreadedProcess process, String name, boolean closeProcessOnShutdown) {
		super("ThreadManagerThread-" + name.trim());
		this.process = process;
		this.name = name;
		this.closeProcessOnShutdown = closeProcessOnShutdown;
	}

	@Override
	public synchronized void run() {
		this.running = true;
		do this.updateThreads(); while (this.running);
		if (this.closeProcessOnShutdown) this.process.shouldClose = true;
	}

	public synchronized void updateThreads() {
		try {
			for (Thread thread : this.threads.values()) if (thread.getState().equals(Thread.State.NEW)) thread.start();
			if (!this.runningPredicate.test(this)) this.running = false;
		} catch (Exception e) {
			this.running = false;
			throw new RuntimeException("An error occurred while updating threads!", e);
		}
	}

	public synchronized <T extends Thread> void addThread(T thread) {
		this.addThread(thread.getName(), thread);
	}

	protected synchronized <T extends Thread> void addThread(String name, T thread) {
		this.threads.put(name, thread);
	}
}