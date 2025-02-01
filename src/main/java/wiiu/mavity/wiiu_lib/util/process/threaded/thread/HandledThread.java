package wiiu.mavity.wiiu_lib.util.process.threaded.thread;

import wiiu.mavity.wiiu_lib.annotation.EffectivelyFinal;

public abstract class HandledThread extends Thread {

	@EffectivelyFinal
	public volatile ThreadManagerThread threadManager;

	public HandledThread(ThreadManagerThread threadManager, String name) {
		super(name + "-" + (threadManager.threads.size() + 1));
		this.threadManager = threadManager;
	}

	@Override
	public synchronized void run() {
		this.threadManager.threads.remove(this.getName());
	}
}