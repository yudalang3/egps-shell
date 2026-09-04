package egps2.builtin.modules.filemanager;

import com.google.common.base.Joiner;

import java.io.Serial;
import java.nio.file.Path;

/**
 * BookmarkElement belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BookmarkElement {
    private String name;
    private Path path;
    private Path rootPath;

    public BookmarkElement(String name, Path path, Path rootPath) {
        this.name = name;
        this.path = path;
        this.rootPath = rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getStorageString() {
        Joiner joiner = Joiner.on("\t");
        String join = joiner.join(getName(),getPath().toString(), getRootPath().toString());
        return join;
    }

    @Override
    public String toString() {
        return name;
    }
}
