package com.open.utils;

import com.open.param.test.TestApi;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-02-26
 */
public class TestHelper {
    public static String readFile(String name) throws IOException {
        String path = TestApi.class.getResource("").getPath();
        int index = path.indexOf("/target/test-classes/");
        String baseDir = path.substring(0, index);
        File filePath = Paths.get(baseDir).resolve("src").resolve("test").resolve("resources")
                .resolve(name).toAbsolutePath().toFile();
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(filePath)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }
}
