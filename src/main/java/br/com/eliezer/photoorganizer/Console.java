package br.com.eliezer.photoorganizer;
import java.io.File;

public class Console {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Invalid dir arg[0]");
		}
		final File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.out.println("Invalid dir ");
		}
		for (File file : dir.listFiles()) {
			PhotoOrganizer.moveFile(file);
		}
	}
}
