package egps2.frame.gui.handler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.InputMismatchException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import egps2.utils.common.util.EGPSShellIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * field is a JTextField
 * field.setTransferHandler(new JTextFieldTransferHandler(field));
 * <p>
 * Users only need to write the inputFilesConsumer.
 *
 */
public class JTextAreaTransferHandler extends TransferHandler {

	private static final Logger log = LoggerFactory.getLogger(JTextAreaTransferHandler.class);
	private final Consumer<List<File>> inputFilesConsumer;

	@Override
	public Icon getVisualRepresentation(Transferable t) {
        return EGPSShellIcons.getHelpIcon();
	}

	public JTextAreaTransferHandler(Consumer<List<File>> inputFilesConsumer) {
		this.inputFilesConsumer = inputFilesConsumer;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		boolean a = t.isDataFlavorSupported(DataFlavor.stringFlavor);
		boolean b = t.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		try {
			if (a) {
				Object o = t.getTransferData(DataFlavor.stringFlavor);
				List<File> listOfInputFile = List.of(new File(o.toString()));
                inputFilesConsumer.accept(listOfInputFile);
				return true;
			} else if (b) {
				Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

				@SuppressWarnings("unchecked")
				List<File> listOfInputFile = (List<File>) o;
				inputFilesConsumer.accept(listOfInputFile);

				return true;
			} else {
				throw new InputMismatchException("Please tell developers, new situation happens.");
			}
		} catch (Exception e) {
			log.error("Import data error", e);
		}

		return false;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                return true;
            }

            if (DataFlavor.stringFlavor.equals(flavor)) {
                return true;
            }
        }
		return false;
	}
}
