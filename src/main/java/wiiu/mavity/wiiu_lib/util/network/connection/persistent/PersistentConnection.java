package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import wiiu.mavity.wiiu_lib.util.ObjectHolder;
import wiiu.mavity.wiiu_lib.util.network.*;
import wiiu.mavity.wiiu_lib.util.network.connection.Connection;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PersistentConnection extends Connection {

	protected final ObjectHolder<Socket> socket = new ObjectHolder<>();
	protected final ObjectHolder<PrintWriter> out = new ObjectHolder<>();
	protected final ObjectHolder<BufferedReader> in = new ObjectHolder<>();
	protected final int port;
	private final InputStream userIn;
	private final OutputStream userOut;
	private final boolean autoFlushConnectionWriter;
	private final boolean autoFlushOutputWriter;
	private final String closeConnectionString;

	protected PersistentConnection(PersistentConnectionBuilder builder) {
		super(
			ConnectionBuilder.create()
				.setURL(builder.url)
		);
		this.port = builder.port;
		this.userIn = builder.in;
		this.userOut = builder.out;
		this.autoFlushConnectionWriter = builder.autoFlushConnectionWriter;
		this.autoFlushOutputWriter = builder.autoFlushOutputWriter;
		this.closeConnectionString = builder.closeConnectionString;
	}

	public InputStream getUserIn() {
		return this.userIn;
	}

	public OutputStream getUserOut() {
		return this.userOut;
	}

	public boolean doAutoFlushConnectionWriter() {
		return this.autoFlushConnectionWriter;
	}

	public boolean doAutoFlushOutputWriter() {
		return this.autoFlushOutputWriter;
	}

	public String getCloseConnectionString() {
		return this.closeConnectionString;
	}

	@Override
	public boolean isOpen() {
		return this.in.isPresent() && this.out.isPresent() && super.isOpen();
	}

	public int getPort() {
		return this.port;
	}

	public void scanInAndWriteResponseToOut() {
		this.checkOpen();
		Scanner scanner = new Scanner(this.getUserIn());
		PrintWriter writer = new PrintWriter(this.getUserOut(), this.doAutoFlushOutputWriter());
		this.scanInAndPost(scanner, writer);
	}

	// TODO: add two Function<String, String> modifiers, one to transform input from scanner into what is printed to connection, and one to transform what is received from connection into output
	public void scanInAndPost(Scanner scanner, PrintWriter writer) {
		this.checkOpen();
		String scannerLine;
		do scannerLine = scanner.nextLine(); while (scannerLine == null);
		if (scannerLine.trim().equals(this.getCloseConnectionString())) {
			this.close();
			return;
		}
		writer.println(this.sendAndAwaitResponse(scannerLine));
		if (!this.doAutoFlushConnectionWriter()) this.flushOut();
		if (!this.doAutoFlushOutputWriter()) writer.flush();
		this.scanInAndPost(scanner, writer);
	}

	public void flushOut() {
		this.checkOpen();
		this.out.getOrThrow().flush();
	}

	public String sendAndAwaitResponse(String message) {
		this.send(message);
		return this.awaitResponse();
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

	public String awaitResponse() {
		this.checkOpen();
		String response;
		do response = this.getResponse(); while (response == null);
		return response;
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
		this.out.set(new PrintWriter(new OutputStreamWriter(out), this.doAutoFlushConnectionWriter()));
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

	public static class PersistentConnectionBuilder {

		protected URL url = NetworkUtil.url("https://www.example.com");
		protected String targetIp = "127.0.0.1";
		protected int port = 6666;
		protected InputStream in;
		protected OutputStream out;
		protected boolean autoFlushConnectionWriter = true;
		protected boolean autoFlushOutputWriter = true;
		protected String closeConnectionString = "stop";

		private PersistentConnectionBuilder() {}

		public static PersistentConnectionBuilder create() {
			return new PersistentConnectionBuilder();
		}

		public PersistentConnectionBuilder setURL(URL url) {
			this.url = url;
			try {
				this.targetIp = InetAddress.getByName(this.url.getHost()).getHostAddress();
			} catch (Exception e) {
				throw new NetworkException(this.url, e);
			}
			return this;
		}

		public PersistentConnectionBuilder setURL(String url) {
			return this.setURL(NetworkUtil.url(url));
		}

		public PersistentConnectionBuilder setPort(int port) {
			this.port = port;
			return this;
		}

		public PersistentConnectionBuilder setPort(Number port) {
			this.port = port.intValue();
			return this;
		}

		public PersistentConnectionBuilder setIn(InputStream in) {
			this.in = in;
			return this;
		}

		public PersistentConnectionBuilder setOut(OutputStream out) {
			this.out = out;
			return this;
		}

		public PersistentConnectionBuilder setAutoFlushConnectionWriter(boolean autoFlushConnectionWriter) {
			this.autoFlushConnectionWriter = autoFlushConnectionWriter;
			return this;
		}

		public PersistentConnectionBuilder enableAutoFlushConnectionWriter() {
			return this.setAutoFlushConnectionWriter(true);
		}

		public PersistentConnectionBuilder disableAutoFlushConnectionWriter() {
			return this.setAutoFlushConnectionWriter(false);
		}

		public PersistentConnectionBuilder setAutoFlushOutputWriter(boolean autoFlushOutputWriter) {
			this.autoFlushOutputWriter = autoFlushOutputWriter;
			return this;
		}

		public PersistentConnectionBuilder enableAutoFlushOutputWriter() {
			return this.setAutoFlushOutputWriter(true);
		}

		public PersistentConnectionBuilder disableAutoFlushOutputWriter() {
			return this.setAutoFlushOutputWriter(false);
		}

		public PersistentConnectionBuilder setCloseConnectionString(String closeConnectionString) {
			this.closeConnectionString = closeConnectionString;
			return this;
		}

		public PersistentConnectionBuilder setTargetIp(String ip) {
			this.targetIp = ip;
			return this;
		}

		private PersistentConnection build() {
			return new PersistentConnection(this);
		}

		public PersistentServerConnection buildServer() {
			return new PersistentServerConnection(this);
		}

		public PersistentClientConnection buildClient() {
			return new PersistentClientConnection(this);
		}
	}
}