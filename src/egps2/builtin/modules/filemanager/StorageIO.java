package egps2.builtin.modules.filemanager;

import egps2.EGPSProperties;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * StorageIO belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class StorageIO {

    final String selectedIndexString = "#Selected.index";

    private final String storagePath = EGPSProperties.JSON_DIR + "/egps.file.manager.txt";

    public void saveBookmarkList(DefaultListModel<BookmarkElement> bookmarkListModel, BookmarkElement selectedBookmarkElement)  throws IOException{
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(storagePath))) {
            int size = bookmarkListModel.size();
            for (int i = 0; i < size; i++) {
                BookmarkElement bookmarkElement = bookmarkListModel.get(i);
                writer.write(bookmarkElement.getStorageString());
                writer.write("\n");
                if (bookmarkElement == selectedBookmarkElement){
                    writer.write(selectedIndexString);
                    writer.write(" ");
                    writer.write(String.valueOf(i));
                    writer.write("\n");
                }
            }
        }
    }

    public int loadBookmarkList(DefaultListModel<BookmarkElement> bookmarkListModel) throws IOException {
        List<String> strings = Files.readAllLines(Path.of(storagePath));
        int selectedIndex = 0;

        for (String string : strings){
            if (string.startsWith(selectedIndexString)){
                String substring = string.substring(selectedIndexString.length(), string.length());
                selectedIndex = Integer.parseInt(substring.strip());
                continue;
            }

            String[] split = string.split("\t");
            BookmarkElement bookmarkElement = new BookmarkElement(split[0], Path.of(split[1]), Path.of(split[2]));
            bookmarkListModel.addElement(bookmarkElement);
        }

        return selectedIndex;
    }


}
