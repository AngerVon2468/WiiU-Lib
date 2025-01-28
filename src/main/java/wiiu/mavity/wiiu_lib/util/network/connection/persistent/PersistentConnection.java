package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import wiiu.mavity.wiiu_lib.util.ObjectHolder;
import wiiu.mavity.wiiu_lib.util.network.NetworkException;
import wiiu.mavity.wiiu_lib.util.network.connection.Connection;

import java.io.*;
import java.net.*;

public class PersistentConnection extends Connection {

	protected final ObjectHolder<Socket> socket = new ObjectHolder<>();
	protected final ObjectHolder<PrintWriter> out = new ObjectHolder<>();
	protected final ObjectHolder<BufferedReader> in = new ObjectHolder<>();
	protected final int port;

	protected PersistentConnection(URL url, int port) {
		super(
			ConnectionBuilder.create()
				.setURL(url)
		);
		this.port = port;
	}

	protected PersistentConnection(int port) {
		super(ConnectionBuilder.create());
		this.port = port;
	}

	@Override
	public boolean isOpen() {
		return this.in.isPresent() && this.out.isPresent() && this.open;
	}

	public int getPort() {
		return this.port;
	}

	public String exchangeAndAwait(String message) {
		this.send(message);
		return this.getResponse();
	}

	public void send(String msg) {
		this.post(msg);
	}

	@Override
	public void post(String toPost) {
		this.checkOpen();
		try {
			this.out.getOrThrow().println(toPost);
		} catch (Exception e) {
			throw new NetworkException(this.getUrl(), e);
		}
	}

	@Override
	public String getResponse() {
		this.checkOpen();
		try {
			return this.in.getOrThrow().readLine();
		} catch (Exception e) {
			throw new NetworkException(this.getUrl(), e);
		}
	}

	@Override
	public PersistentConnection open() {
		try {
			this.open0();
			this.open = true;
		} catch (Exception e) {
			this.open = false;
			throw new NetworkException(this.getUrl(), e);
		}
		return this;
	}

	public void open0() {
		try {
			var socket = this.socket.getOrThrow("Socket should be non-null by this point!");
			this.open0(socket.getInputStream(), socket.getOutputStream());
		} catch (Exception e) {
			throw new NetworkException(this.getUrl(), e);
		}
	}

	public void open0(InputStream in, OutputStream out) {
		this.in.set(new BufferedReader(new InputStreamReader(in)));
		this.out.set(new PrintWriter(new OutputStreamWriter(out), true));
	}

	@Override
	public void close() {
		this.close0();
		this.open = false;
	}

	public void close0() {
		this.in.ifPresent((in) -> {
			try {
				in.close();
			} catch (Exception e) {
				throw new NetworkException(this.getUrl(), e);
			}
		});
		this.out.ifPresent((out) -> {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				throw new NetworkException(this.getUrl(), e);
			}
		});
		this.socket.ifPresent((socket) -> {
			try {
				socket.close();
			} catch (Exception e) {
				throw new NetworkException(this.getUrl(), e);
			}
		});
	}
}