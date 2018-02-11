package com.pixurvival.core.contentPack;

import java.io.File;

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
}
