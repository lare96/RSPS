package com.vencillio.core.task.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.vencillio.core.task.Task;

/**
 * Digging with a spade
 */
public class PlayerBackupTask extends Task {

	public PlayerBackupTask() {
		super(72000, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CHARACTER_BACKUP);
	}

	@Override
	public void execute() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				backup();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
	}

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				backup();
				System.out.println("done.");
			}
		});
		try {
			t.start();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void backup() {
		String charDir = "." + File.separator + "data" + File.separator + "characters" + File.separator;
		String backupFolder = System.getProperty("user.home") + File.separator + "vencillio-backups" + File.separator + new SimpleDateFormat("[EE MMM d][h.mma]").format(new Date()) + File.separator + "characters" + File.separator;

		File folder = new File(backupFolder + "containers");
		folder.mkdirs();
		
		folder = new File(backupFolder + "details");
		folder.mkdirs();

		folder = new File(backupFolder + "farming");
		folder.mkdirs();

		for (File file : new File(charDir + "containers" + File.separator).listFiles()) {
			try {
				copyFile(file, new File(backupFolder + "containers" + File.separator + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (File file : new File(charDir + "details" + File.separator).listFiles()) {
			try {
				copyFile(file, new File(backupFolder + "details" + File.separator + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (File file : new File(charDir + "farming" + File.separator).listFiles()) {
			try {
				copyFile(file, new File(backupFolder + "farming" + File.separator + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (File file : new File(System.getProperty("user.home") + File.separator + "vencillio-backups").listFiles()) {
			if (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - file.lastModified()) >= 14) {
				String[] entries = file.list();

				for (String entry : entries) {
					File currentFile = new File(file.getPath(), entry);
					currentFile.delete();
				}

				file.delete();
			}
		}

		System.out.println("Character file backup generated. Time - " + new SimpleDateFormat("[EE MMM d][h.mma]").format(new Date()));
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		Files.copy(Paths.get(sourceFile.getPath()), Paths.get(destFile.getPath()), StandardCopyOption.COPY_ATTRIBUTES);
	}
}