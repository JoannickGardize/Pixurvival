package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class WorkingDirectory {

    public static File get() {
        File file = new File(System.getProperty("user.home"), ".pixurvival");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
}
