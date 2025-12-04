package test.plugin;

import java.awt.BorderLayout;

import org.apache.commons.lang3.tuple.Triple;

import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;

/**
 * <pre>
 * |---------------------------------------------------------------|
 * |mainSplitPanel              |                                  |
 * |                            |                                  |
 * |LeftControlPanelContainner  |  rightSplitPane                  |
 * |                            |                                  |
 * |                            |  tabbedPhylogeneticTreePane      | 
 * |                            |                                  |
 * |                            | ---------------------------------|
 * |                            |                                  |
 * |                            |  tabbedAnalysisPanel             | 
 * |---------------------------------------------------------------|
 * 
 * </pre>
 * 
 * @author yudalang
 *
 */
@SuppressWarnings("serial")
public class XXXMainFace extends ModuleFace {

	XXXMainFace(IModuleLoader moduleLoader) {
		super(moduleLoader);
		
		setLayout(new BorderLayout());
	}

	@Override
	public boolean closeTab() {

		return false;
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

	}

	@Override
	public boolean canExport() {
		return true;
	}

	@Override
	public void exportData() {
	}

	@Override
	public void initializeGraphics() {
		WorkBanch4XXX operator = new WorkBanch4XXX();
		add(operator, BorderLayout.CENTER);
	}

	@Override
	public String[] getFeatureNames() {
		return null;
	}




}
