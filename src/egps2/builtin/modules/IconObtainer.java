package egps2.builtin.modules;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Provides a single static method for loading icons as resources.
 */
public class IconObtainer {

    /**
     * Load an icon as a resource from the "images" directory.
     */
    public static ImageIcon get(String name) {
        return get(name, null);
    }

    public static ImageIcon get(String name, String description) {

        URL resource = IconObtainer.class.getResource("images/" + name);

        ImageIcon imageIcon;
        if (description == null) {
            imageIcon = new ImageIcon(resource);
        } else {
            imageIcon = new ImageIcon(resource, description);
        }

        return imageIcon;
    }
}