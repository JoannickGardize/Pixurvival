package com.pixurvival.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			deleteDirectoryRecursively(file);
		}
	}

	@SneakyThrows
	public static void deleteDirectoryRecursively(File file) {
		Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw e;
				}
			}
		});
	}

	/**
	 * Read bytes from a File into a byte[].
	 * 
	 * @param file
	 *            The File to read.
	 * @return A byte[] containing the contents of the File.
	 * @throws IOException
	 *             Thrown if the File is too long to read or couldn't be read fully.
	 */
	public static byte[] readBytes(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {

			// Get the size of the file
			long length = file.length();

			// Create the byte array to hold the data
			byte[] bytes = new byte[(int) length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}

			// Close the input stream and return bytes
			return bytes;
		}
	}

	public static byte[] readBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] result = buffer.toByteArray();
		buffer.close();
		is.close();
		return result;
	}

	/**
	 * Writes the specified byte[] to the specified File path.
	 * 
	 * @param theFile
	 *            File Object representing the path to write to.
	 * @param bytes
	 *            The byte[] of data to write to the File.
	 * @throws IOException
	 *             Thrown if there is problem creating or writing the File.
	 */
	public static void writeBytes(File theFile, byte[] bytes) throws IOException {
		// BufferedOutputStream bos = null;

		try (FileOutputStream fos = new FileOutputStream(theFile)) {
			// bos = new BufferedOutputStream(fos);
			fos.write(bytes);
		}
	}

}
