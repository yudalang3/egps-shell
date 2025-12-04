package test.plugin;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.Triple;

import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;

/**
 * MainFace supports the plugin/template system for extending eGPS.
 */
public class MainFace extends ModuleFace{
	
	
	MainFace(IModuleLoader moduleLoader) {
		super(moduleLoader);
		
		setLayout(new BorderLayout());
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		JLabel comp = new JLabel("这是Alignment view 模块");
		Font font = comp.getFont().deriveFont(30f);
		comp.setFont(font);
		jPanel.add(comp);
		
		add(jPanel, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}

	@Override
	public boolean closeTab() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeToThisTab() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean canImport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void importData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canExport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exportData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeGraphics() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getFeatureNames() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
