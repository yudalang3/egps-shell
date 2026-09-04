package egps2.builtin.modules.filemanager;

import egps2.EGPSProperties;

import java.nio.file.Path;

/**
 * PropertiesSetter belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class PropertiesSetter {

    public BookmarkElement getDefaultDir(){
        Path path = Path.of(EGPSProperties.PROPERTIES_DIR);
        var ret = new BookmarkElement("eGPS properties dir.", path, path );
        return ret;
    }
}
