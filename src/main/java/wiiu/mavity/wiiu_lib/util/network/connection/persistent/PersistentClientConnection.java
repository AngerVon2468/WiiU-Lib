package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import wiiu.mavity.wiiu_lib.util.network.*;

import java.net.*;

public class PersistentClientConnection extends PersistentConnection {

	protected final String targetIp;

	public PersistentClientConnection(URL url, int port) {
		super(url, port);
		try {
			this.targetIp = InetAddress.getByName(this.getUrl().getHost()).getHostAddress();
		} catch (Exception e) {
			throw new NetworkException(this.getUrl(), e);
		}
	}

	public PersistentClientConnection(String ip, int port) {
		super(port);
		this.targetIp = ip;
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