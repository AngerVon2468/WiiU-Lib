package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import wiiu.mavity.wiiu_lib.util.network.*;

import java.net.*;

public class PersistentClientConnection extends PersistentConnection {

	protected final String targetIp;

	public PersistentClientConnection(PersistentConnectionBuilder builder) {
		super(builder);
		this.targetIp = builder.targetIp;
	}

	public String getTargetIp() {
		return this.targetIp;
	}

	@Override
	public PersistentClientConnection open() {
		try {
			this.socket.set(new Socket(this.getTargetIp(), this.getPort()));
			super.open();
		} catch (Exception e) {
			this.open = false;
			throw new NetworkException(this.getUrl(), e);
		}
		return this;
	}

	@Override
	public void close() {
		super.close();
	}
}