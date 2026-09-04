package egps2.builtin.modules.lowtextedi;

import java.awt.BorderLayout;
import java.util.Objects;

import egps2.UnifiedAccessPoint;
import egps2.frame.ModuleFace;
import egps2.modulei.IInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * TextViewMainFace belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class TextViewMainFace extends ModuleFace{

	private Editor editor;
	private final ImportDataInfo originalContent;

	TextViewMainFace(IndependentModuleLoader moduleLoader, ImportDataInfo str) {
		super(moduleLoader);
		String content = str.getContent();


		editor = new Editor(content);
		add(editor.getMain(), BorderLayout.CENTER);
		setBorder(null);
		this.originalContent = str;
		//不能是原始的string而应该是editor.getText()
		this.originalContent.setContent(editor.getText());
	}

	@Override
	public boolean closeTab() {
		String text = editor.getText();
		boolean isChanged = !Objects.equals(text, originalContent.getContent());
		return isChanged;
	}

	@Override
	public void changeToThisTab() {
		
	}


	@Override
	public boolean canImport() {
		return true;
	}

	@Override
	public void importData() {
		editor.loadFile();
		invokeTheFeatureMethod(0);
	}

	@Override
	public boolean canExport() {
		return true;
	}

	@Override
	public void exportData() {
		String exportedString = editor.saveAs(originalContent);
		if (exportedString!=null){
			this.originalContent.setContent(exportedString);
		}
	}

	@Override
	public void initializeGraphics() {
		editor.focusMe();
	}

	@Override
	public String[] getFeatureNames() {
		return new String[] { "View and edit low volume text" };
	}


	@Override
	public IInformation getModuleInfo() {
		IInformation iInformation = new IInformation() {

			@Override
			public String getWhatDataInvoked() {
				return "The data is loading from the import dialog.";
			}

			@Override
			public String getSummaryOfResults() {
				return "The functionality is powered by the eGPS software.";
			}
		};
		return iInformation;
	}
}
