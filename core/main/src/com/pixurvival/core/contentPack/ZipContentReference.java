package com.pixurvival.core.contentPack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ZipContentReference {
	private File zipFile;
	private String entryName;

	public byte[] read() throws ContentPackReadException {
		try (ZipFile zipFile = new ZipFile(this.zipFile)) {
			ZipEntry entry = zipFile.getEntry(entryName);
			InputStream is = zipFile.getInputStream(entry);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new ContentPackReadException(e);
		}
	}
}
