package com.vencillio.core.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SystemLogger extends PrintStream {

	// stdout and stderr share file names.
	private static final long CLASS_LOAD_TIME = System.currentTimeMillis();
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final File outFile;

	public SystemLogger(OutputStream out, File directory) {
		super(out);

		String fileName = CLASS_LOAD_TIME + " - " + System.getProperty("user.name") + " - "  + new SimpleDateFormat("[EE MMM d][h.mma]").format(CLASS_LOAD_TIME) + ".txt";
		Objects.requireNonNull(directory, "directory");
		outFile = new File(directory, fileName);

		init();
	}

	private void init() {
		File directory = outFile.getParentFile();

		if (!directory.exists() && !directory.mkdirs()) {
			throw new RuntimeException("Unable to create logging directory: " + directory.getAbsolutePath());
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, false))) {
			bw.write("Logging started at " + LocalDateTime.now().format(DATE_FORMATTER));
			bw.newLine();
			bw.write("File name: " + outFile.getAbsolutePath());
			bw.newLine();
			bw.write("System.getProperty(\"os.name\"): \"");
			bw.write(System.getProperty("os.name"));
			bw.write('"');
			bw.newLine();
			bw.newLine();
		} catch (IOException ignored) {
		}
	}

	@Override
	public void print(String message) {
		String messageWithTimePrefix = "[" + LocalDateTime.now().format(DATE_FORMATTER) + "] " + message;

		try {
			// loot new writer as we're not expecting calls often
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true));
			writer.write(messageWithTimePrefix);
			writer.newLine();
			writer.close();
		} catch (IOException ignored) {
			// no recursive calls to #printStackTrace()...
		}

		super.print(messageWithTimePrefix);
	}

}