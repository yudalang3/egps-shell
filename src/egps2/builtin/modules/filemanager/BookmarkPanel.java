package egps2.builtin.modules.filemanager;

import egps2.UnifiedAccessPoint;
import egps2.panels.dialog.SwingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * BookmarkPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BookmarkPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(BookmarkPanel.class);
    private final GuiMain guiMain;

    final JList<BookmarkElement> bookmarkElementJList = new JList<>();

    public BookmarkPanel(GuiMain guiMain) {
        this.guiMain = guiMain;
        setLayout(new BorderLayout());
        initComp();
    }

    public BookmarkElement getSelectedBookmarkElement() {
        BookmarkElement selectedValue = bookmarkElementJList.getSelectedValue();
        return selectedValue;
    }

    private void initComp() {
        // 初始化组件
        StorageIO storageIO = guiMain.storageIO;
        DefaultListModel<BookmarkElement> bookmarkListModel = guiMain.bookmarkListModel;

        PropertiesSetter propertiesSetter = guiMain.propertiesSetter;

        int selectedIndex = configureBookmarkList(storageIO, bookmarkListModel, propertiesSetter);

        bookmarkElementJList.setModel(bookmarkListModel);

        JScrollPane bookmarkScrollPane = new JScrollPane(bookmarkElementJList);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("The dir. bookmark");
        bookmarkScrollPane.setBorder(titledBorder);

        titledBorder.setTitleFont(guiMain.defaultTitleFont);
        bookmarkElementJList.setFont(guiMain.defaultFont);

        addPopupMenu(bookmarkElementJList);

        handleListSelection(bookmarkElementJList);

        JPanel buttonPanel = createButtonPanel(bookmarkListModel, bookmarkElementJList, propertiesSetter);
        bookmarkElementJList.setSelectedIndex(selectedIndex);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(bookmarkScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private int configureBookmarkList(StorageIO storageIO, DefaultListModel<BookmarkElement> bookmarkListModel, PropertiesSetter propertiesSetter) {
        int selectedIndex = 0;
        try {
            selectedIndex = storageIO.loadBookmarkList(bookmarkListModel);
        } catch (Exception e) {
            bookmarkListModel.addElement(propertiesSetter.getDefaultDir());
            SwingDialog.showInfoMSGDialog("Initialization", "This is the default bookmark.");
            log.info("Initialize the bookmark list.");
        }
        if (bookmarkListModel.isEmpty()) {
            bookmarkListModel.addElement(propertiesSetter.getDefaultDir());
        }
        return selectedIndex;
    }

    private void addPopupMenu(JList<BookmarkElement> bookmarkElementJList) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        popupMenu.add(renameMenuItem);

        bookmarkElementJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = bookmarkElementJList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        bookmarkElementJList.setSelectedIndex(index);
                        popupMenu.show(bookmarkElementJList, e.getX(), e.getY());
                    }
                }
            }
        });
        renameMenuItem.addActionListener(e -> {
            BookmarkElement selectedElement = bookmarkElementJList.getSelectedValue();
            if (selectedElement != null) {
                String newName = JOptionPane.showInputDialog(
                        guiMain,
                        "Enter a new name for the bookmark:",
                        "Rename Bookmark",
                        JOptionPane.QUESTION_MESSAGE
                );
                if (newName != null && !newName.trim().isEmpty()) {
                    selectedElement.setName(newName);
                    bookmarkElementJList.repaint();
                }
            }
        });
    }

    private void handleListSelection(JList<BookmarkElement> bookmarkElementJList) {
        // JList 的 addElement方法也会触发这个事件！
        bookmarkElementJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            //System.out.println(e.getSource() + " is e.getSource()");
            // This is the JList, not the selected element
            BookmarkElement selectedElement = bookmarkElementJList.getSelectedValue();
            //System.out.println(selectedElement == e.getSource());
            if (selectedElement == null || guiMain.explorerPanel == null) {
                return;
            }
            Path selectedPath = selectedElement.getPath();
            if (Files.notExists(selectedPath)) {
                JOptionPane.showMessageDialog(
                        guiMain,
                        "The selected path does not exist: " + selectedPath.toString(),
                        "Path Not Found",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            guiMain.explorerPanel.updateFilePath4bookmarkJListClick(selectedElement);
            guiMain.fileInfoPanel.changeFile(selectedPath);
        });
    }

    private JPanel createButtonPanel(DefaultListModel<BookmarkElement> bookmarkListModel, JList<BookmarkElement> bookmarkElementJList, PropertiesSetter propertiesSetter) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete");

        createButton.setFont(guiMain.defaultFont);
        deleteButton.setFont(guiMain.defaultFont);

        createButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(UnifiedAccessPoint.getInstanceFrame()) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                BookmarkElement selectedElement = bookmarkElementJList.getSelectedValue();
                BookmarkElement bookmarkElement = new BookmarkElement(selectedFile.getName(), selectedFile.toPath(), selectedFile.toPath());
                if (selectedElement != null) {
                    int selectedIndex = bookmarkElementJList.getSelectedIndex();
                    bookmarkListModel.insertElementAt(bookmarkElement, selectedIndex + 1);
                } else {
                    bookmarkListModel.addElement(bookmarkElement);
                }
                bookmarkElementJList.setSelectedValue(bookmarkElement, true);
            }

            guiMain.invokeTheFeatureMethod(0);
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = bookmarkElementJList.getSelectedIndex();
            if (selectedIndex != -1) {
                bookmarkListModel.remove(selectedIndex);
                if (bookmarkListModel.isEmpty()) {
                    BookmarkElement defaultDir = propertiesSetter.getDefaultDir();
                    bookmarkListModel.addElement(defaultDir);
                }
                guiMain.invokeTheFeatureMethod(1);
            }
        });

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        bottomPanel.add(createButton);
        bottomPanel.add(deleteButton);
        buttonPanel.add(bottomPanel, BorderLayout.SOUTH);

        return buttonPanel;
    }

    public void appendNewBookmark(BookmarkElement bookmarkElement) {
        DefaultListModel<BookmarkElement> model = (DefaultListModel<BookmarkElement>) bookmarkElementJList.getModel();
        model.addElement(bookmarkElement);
        bookmarkElementJList.setSelectedValue(bookmarkElement, true);
    }
}
