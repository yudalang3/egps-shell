package egps2.builtin.modules.lowtextedi;

import java.io.File;
import java.util.Optional;

/**
 * ImportDataInfo belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ImportDataInfo {
    String content = "";
    Optional<File> inputFile = Optional.empty();

    public ImportDataInfo() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Optional<File> getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = Optional.of(inputFile);
    }
}
