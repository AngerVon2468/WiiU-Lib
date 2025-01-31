package wiiu.mavity.wiiu_lib.util.process;

public abstract class LoopableProcess implements ResourceContainer {

	public volatile boolean shouldClose = false;

	public synchronized void createResources() {}

	public synchronized void loop() {
		do this.loop0(); while (!this.shouldClose);
	}

	protected synchronized void loop0() {}

	@Override
	public synchronized LoopableProcess open() {
		return this;
	}

	@Override
	public synchronized void close() {
		if (!this.shouldClose) this.shouldClose = true;
	}
}