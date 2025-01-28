package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import wiiu.mavity.wiiu_lib.util.ObjectHolder;
import wiiu.mavity.wiiu_lib.util.network.NetworkException;

import java.net.*;

public class PersistentServerConnection extends PersistentConnection {

	protected final ObjectHolder<ServerSocket> serverSocket = new ObjectHolder<>();
	protected final ObjectHolder<Socket> clientSocket = new ObjectHolder<>();

	public PersistentServerConnection(PersistentConnectionBuilder builder) {
		super(builder);
	}

	@Override
	public PersistentServerConnection open() {
		try {
			this.serverSocket.set(new ServerSocket(this.getPort()));
			this.clientSocket.set(this.serverSocket.getOrThrow().accept());
			var socket = this.clientSocket.getOrThrow();
			this.open0(socket.getInputStream(), socket.getOutputStream());
			this.open = true;
		} catch (Exception e) {
			this.open = false;
			throw new NetworkException(e);
		}
		return this;
	}

	@Override
	public void close() {
		this.close0();
		this.open = false;
	}

	@Override
	public void close0() {
		super.close0();
		this.serverSocket.ifPresent((socket) -> {
			try {
				socket.close();
			} catch (Exception e) {
				throw new NetworkException(e);
			}
		});
		this.clientSocket.ifPresent((socket) -> {
			try {
				socket.close();
			} catch (Exception e) {
				throw new NetworkException(e);
			}
		});
	}
}