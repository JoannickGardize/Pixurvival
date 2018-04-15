package com.pixurvival.core.util;

import java.io.File;
import java.io.IOException;
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
}
