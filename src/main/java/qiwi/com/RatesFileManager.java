package qiwi.com;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class RatesFileManager {

    Path loadDataPath = Path.of(System.getProperty("user.dir"), "/resources", "rates.xml");

    public boolean save(String lines) {
        try {
            Files.createFile(loadDataPath);
            Files.write(loadDataPath, Collections.singleton(lines), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
