package wiiu.mavity.wiiu_lib.util.process.threaded;

import wiiu.mavity.wiiu_lib.annotation.EffectivelyFinal;
import wiiu.mavity.wiiu_lib.util.process.LoopableProcess;
import wiiu.mavity.wiiu_lib.util.process.threaded.thread.ThreadManagerThread;

public abstract class LoopableMultiThreadedProcess extends LoopableProcess {

	@EffectivelyFinal
	public volatile ThreadManagerThread threadManager;

	@Override
	public synchronized LoopableMultiThreadedProcess open() {
		this.threadManager.start();
		return this;
	}

	@Override
	public synchronized void close() {
		super.close();
		this.threadManager.running = false;
	}

	public synchronized <T extends Thread> void addThread(T thread) {
		this.threadManager.addThread(thread);
	}
}