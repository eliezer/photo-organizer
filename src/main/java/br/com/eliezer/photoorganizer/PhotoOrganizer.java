package br.com.eliezer.photoorganizer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;

public class PhotoOrganizer {

	public static void moveFile(File file) throws Exception {
		if (file.isFile() && file.getName().toLowerCase().endsWith("jpg")) {
			final String dirName = new SimpleDateFormat("dd-MM-yyyy").format(findOrignDateFile(file));
			final File destination = new File(file.getParentFile(), dirName + File.separator + file.getName());
			if (!destination.getParentFile().exists()) {
				destination.getParentFile().mkdirs();
			}
			System.out.println("Move '" + file.getAbsolutePath() + "' TO '" + destination + "'");
			file.renameTo(destination);
		} else {
			System.out.println("Skip file:" + file);
		}
	}

	private static Date findOrignDateFile(File file) throws Exception {
		final Metadata metadata = new ExifReader(new JpegSegmentReader(file).readSegment(JpegSegmentReader.SEGMENT_APP1)).extract();
		return metadata.getDirectory(ExifDirectory.class).getDate(ExifDirectory.TAG_DATETIME_ORIGINAL);
	}
}
