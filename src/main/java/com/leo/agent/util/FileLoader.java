package com.leo.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class FileLoader {
    private FileLoader() {
    }

    public static InputStream load(final String path) {
        URL url = FileLoader.class.getResource(path);
        if (url != null) {
            return FileLoader.class.getResourceAsStream(path);
        } else {
            String dir = System.getProperty("user.dir");
            if (dir == null) {
                return null;
            } else {
                try {
                    return new FileInputStream(new File(dir.replaceAll("\\\\", "//"),
                            path.replaceAll("\\\\", "//")));
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
