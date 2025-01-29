package wiiu.mavity.wiiu_lib.util.network.connection.persistent;

import com.google.gson.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import wiiu.mavity.wiiu_lib.util.*;
import wiiu.mavity.wiiu_lib.util.network.*;
import wiiu.mavity.wiiu_lib.util.network.connection.Connection;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.function.*;

@SuppressWarnings({"UnusedReturnValue", "unchecked"})
public class PersistentConnection extends Connection {

	protected final ObjectHolder<Socket> socket = new ObjectHolder<>();
	protected final ObjectHolder<PrintWriter> out = new ObjectHolder<>();
	protected final ObjectHolder<BufferedReader> in = new ObjectHolder<>();
	protected final int port;
	protected final InputStream userIn;
	protected final OutputStream userOut;
	protected final boolean autoFlushConnectionWriter;
	protected final boolean autoFlushOutputWriter;
	protected final String closeConnectionString;
	protected final Function<String, String> userToConnectionModifier;
	protected final Function<String, String> connectionToUserModifier;
	protected final BiFunction<Object, PersistentConnection, String> userToConnectionModifierJson;
	protected final TriFunction<String, PersistentConnection, ?, ?> connectionToUserModifierJson;
	protected final Gson gson;
	protected final Class<?> jsonReturnType;

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
		this.userToConnectionModifier = builder.userToConnectionModifier;
		this.connectionToUserModifier = builder.connectionToUserModifier;
		this.gson = builder.gson;
		this.jsonReturnType = builder.jsonReturnType;
		this.userToConnectionModifierJson = builder.userToConnectionModifierJson;
		this.connectionToUserModifierJson = builder.connectionToUserModifierJson;
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

	public Function<String, String> getUserToConnectionModifier() {
		return this.userToConnectionModifier;
	}

	public Function<String, String> getConnectionToUserModifier() {
		return this.connectionToUserModifier;
	}

	public Gson getGson() {
		return this.gson;
	}

	public Class<?> getJsonReturnType() {
		return this.jsonReturnType;
	}

	public BiFunction<Object, PersistentConnection, String> getUserToConnectionModifierJson() {
		return this.userToConnectionModifierJson;
	}

	public <R> TriFunction<String, PersistentConnection, Class<R>, R> getConnectionToUserModifierJson() {
		return (TriFunction<String, PersistentConnection, Class<R>, R>) this.connectionToUserModifierJson;
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

	public void scanInAndPost(Scanner scanner, PrintWriter writer) {
		this.checkOpen();
		String scannerLine;
		do scannerLine = scanner.nextLine(); while (scannerLine == null);
		if (scannerLine.trim().equals(this.getCloseConnectionString())) {
			this.close();
			return;
		}
		writer.println(
			this.getConnectionToUserModifier().apply(
				this.sendAndAwaitResponse(
					this.getUserToConnectionModifier().apply(scannerLine)
				)
			)
		);
		if (!this.doAutoFlushConnectionWriter()) this.flushOut();
		if (!this.doAutoFlushOutputWriter()) writer.flush();
		this.scanInAndPost(scanner, writer);
	}

	public void flushOut() {
		this.checkOpen();
		this.out.getOrThrow().flush();
	}

	public Object sendAndAwaitResponseObj(Object msg) {
		return this.sendAndAwaitResponse0(msg, this.getJsonReturnType());
	}

	public <T> T sendAndAwaitResponse0(Object message, Class<T> returnType) {
		this.send(message);
		return (T) this.getConnectionToUserModifierJson().apply(this.awaitResponse(), this, (Class<Object>) returnType);
	}

	public String sendAndAwaitResponse(String msg) {
		return this.sendAndAwaitResponse0(msg, String.class);
	}

	public void send(Object msg) {
		this.post(msg);
	}

	public void send(String msg) {
		this.post(msg);
	}

	public void post(Object msg) {
		if (msg == null) return;
		this.post0(this.getUserToConnectionModifierJson().apply(msg, this));
	}

	@Override
	public void post(String msg) {
		this.post((Object)msg);
	}

	@Internal
	private void post0(String toPost) {
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
		protected Function<String, String> userToConnectionModifier = Function.identity();
		protected Function<String, String> connectionToUserModifier = Function.identity();
		protected BiFunction<Object, PersistentConnection, String> userToConnectionModifierJson = this.toJson();
		protected TriFunction<String, PersistentConnection, Class<?>, ?> connectionToUserModifierJson = this.fromJson();
		protected Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		protected Class<?> jsonReturnType = String.class;

		private BiFunction<Object, PersistentConnection, String> toJson() {
			return (object, connection) -> connection.getGson().toJson(object);
		}

		private TriFunction<String, PersistentConnection, Class<?>, ?> fromJson() {
			return (json, connection, clazz) -> connection.getGson().fromJson(json, clazz);
		}

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

		public PersistentConnectionBuilder setUserToConnectionModifier(Function<String, String> userToConnectionModifier) {
			this.userToConnectionModifier = userToConnectionModifier;
			return this;
		}

		public PersistentConnectionBuilder setConnectionToUserModifier(Function<String, String> connectionToUserModifier) {
			this.connectionToUserModifier = connectionToUserModifier;
			return this;
		}

		public PersistentConnectionBuilder setGson(GsonBuilder builder) {
			this.gson = builder.create();
			return this;
		}

		public PersistentConnectionBuilder setGson(Gson gson) {
			this.gson = gson;
			return this;
		}

		public PersistentConnectionBuilder setJsonReturnType(Class<?> jsonReturnType) {
			this.jsonReturnType = jsonReturnType;
			return this;
		}

		public PersistentConnectionBuilder setUserToConnectionModifierJson(BiFunction<Object, PersistentConnection, String> userToConnectionModifierJson) {
			this.userToConnectionModifierJson = userToConnectionModifierJson;
			return this;
		}

		public PersistentConnectionBuilder setConnectionToUserModifierJson(TriFunction<String, PersistentConnection, Class<?>, ?> connectionToUserModifierJson) {
			this.connectionToUserModifierJson = connectionToUserModifierJson;
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