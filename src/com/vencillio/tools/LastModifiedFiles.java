package com.vencillio.tools;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.vencillio.core.util.LineCounter.Filter;

public class LastModifiedFiles {
	public static LinkedList<File> listRecursive(File file, Filter<File> filter) {
		LinkedList<File> files = new LinkedList<File>();
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				files.addAll(listRecursive(f, filter));
			} else {
				if (filter.accept(f))
					files.add(f);
			}
		}
		return files;
	}

	public static void main(String[] args) throws IOException {
		List<File> files = listRecursive(new File("./src/"), t -> t.getName().endsWith(".java"));

		for (File file : files) {
			DateFormat dateFormat = new SimpleDateFormat("MM:dd:HH:mm");
			int month = Integer.parseInt(dateFormat.format(file.lastModified()).split(":")[0]);
			int day = Integer.parseInt(dateFormat.format(file.lastModified()).split(":")[1]);
			int hour = Integer.parseInt(dateFormat.format(file.lastModified()).split(":")[2]);
			int minute = Integer.parseInt(dateFormat.format(file.lastModified()).split(":")[3]);
			if (month == 8 && day == 1 && hour == 20) {
				System.out.println(file.getName() + " " + hour + ":" + minute);
			}
		}
	}
}