package wiiu.mavity.wiiu_lib.util;

import java.io.*;
import java.util.StringJoiner;

/**
 * Utility class for working with files.
 */
public class FileUtils {

	/**
	 * Not to be instanced.
	 */
	private FileUtils() {}

	/**
	 * Creates the file if it doesn't exist, and makes it writable and readable.
	 * @return The {@link File} object, for chaining.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static File forceFileExistence(File file) throws IOException {
		if (!file.exists()) file.createNewFile();
		file.setWritable(true);
		file.setReadable(true);
		return file;
	}

	/**
	 * @return A new {@link File} object with the given name in the current working directory.
	 */
	public static File getLocalFile(String fileName) {
		return new File(System.getProperty("user.dir"), fileName);
	}

	public static File getLocalFile(String parent, String fileName) {
		return new File(FileUtils.getLocalFile(parent), fileName);
	}

	/**
	 * @return The contents of the {@link File} object, separated by newlines.
	 */
	public static String getFileContents(File file) throws IOException {
		var reader = new BufferedReader(new FileReader(file));
		StringJoiner sj = new StringJoiner(System.lineSeparator());
		reader.lines().forEach(sj::add);
		reader.close();
		return sj.toString();
	}

	/**
	 * Sets the contents of the {@link File} object if it has no contents already.
	 */
	public static void setContentsIfEmpty(File file, String newContents) throws IOException {
		if (FileUtils.isFileEmpty(file)) FileUtils.setFileContents(file, newContents);
	}

	public static void setFileContents(File file, String newContents) throws IOException {
		var f = new FileWriter(file);
		f.write(newContents);
		f.close();
	}

	public static boolean isFileEmpty(File file) throws IOException {
		String contents = FileUtils.getFileContents(file);
		return contents.isEmpty() || contents.isBlank();
	}
}