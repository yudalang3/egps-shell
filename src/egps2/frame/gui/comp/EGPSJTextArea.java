package egps2.frame.gui.comp;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

import egps2.frame.gui.handler.EGPSTextTransferHandler;

/**
 * 莫非这个JTextArea 可以实现撤销？
 * @author yudalang
 */
public final class EGPSJTextArea extends JTextArea {
	
	private UndoManager undoManager = new UndoManager();

	public EGPSJTextArea() {
		setLineWrap(true);
		
		EGPSTextTransferHandler egpsTextTransferHandler = new EGPSTextTransferHandler();
		this.setTransferHandler(egpsTextTransferHandler);
		
		this.getDocument().addUndoableEditListener(undoManager);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.isControlDown()) {

					switch (evt.getKeyCode()) {
					case KeyEvent.VK_Z:
						if (undoManager.canUndo()) {
							undoManager.undo();
						}
						break;
					case KeyEvent.VK_Y:
						if (undoManager.canRedo()) {
							undoManager.redo();
						}
						break;
// Ctrl + C V X 这三个默认已经实现了。不用管
					default:
						break;
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
	}
}
