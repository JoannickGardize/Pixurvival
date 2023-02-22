package com.pixurvival.gdxcore.lobby;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Save {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private @Getter File file;
    private @Getter LocalDateTime creationTime;
    private String toString;

    public Save(File file) {
        this.file = file;
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            ZonedDateTime zonedDateTime = attr.creationTime().toInstant().atZone(ZoneId.systemDefault());
            creationTime = zonedDateTime.toLocalDateTime();
            LocalDate date = zonedDateTime.toLocalDate();
            toString = date.format(formatter) + " - " + file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            toString = file.getName();
        }
    }

    @Override
    public String toString() {
        return toString;
    }
}
