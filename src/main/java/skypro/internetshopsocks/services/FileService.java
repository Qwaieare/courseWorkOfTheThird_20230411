package skypro.internetshopsocks.services;


import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.nio.file.Path;
@Validated
public interface FileService {


    boolean saveToFile(String json);

    boolean cleanDataFile();

    String readFromFile();

    File getDataFile();

    Path createTempFile(String suffix);

    void createNewFileIfNotExist(String dataFileName);
}
