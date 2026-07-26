package org.example.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void ensureDataDirectoryExists() {
        String dataPath = System.getProperty("user.dir") + "/src/main/java/org/example/data";
        File dataDir = new File(dataPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Tạo các file data nếu chưa tồn tại
        String[] dataFiles = {"books.txt", "members.txt", "reviews.txt", "admin.txt"};
        for (String file : dataFiles) {
            File f = new File(dataPath + "/" + file);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    System.err.println("Cannot create file: " + file);
                }
            }
        }
    }
}
