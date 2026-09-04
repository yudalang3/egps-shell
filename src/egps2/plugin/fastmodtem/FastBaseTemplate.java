package egps2.plugin.fastmodtem;

import java.awt.BorderLayout;

import egps2.EGPSProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.modulei.ModuleVersion;

@SuppressWarnings("serial")
/**
 * FastBaseTemplate supports the plugin/template system for extending eGPS.
 */
public class FastBaseTemplate extends ModuleFace implements IModuleLoader{

	public FastBaseTemplate() {
		this(null);
	}

	protected FastBaseTemplate(IModuleLoader moduleLoader) {
		super(moduleLoader);
		setLayout(new BorderLayout());
	}
	
	@Override
	public int[] getCategory() {
		return null;
	}

	@Override
	public String getShortDescription() {
		return "This is the fast base template.";
	}

    @Override
    public ModuleVersion getVersion() {
        return EGPSProperties.MAINFRAME_CORE_VERSION;
    }

    @Override
	public ModuleFace getFace() {
		return this;
	}

	@Override
	public String getTabName() {
		return "Fast plug-in template";
	}

	@Override
	public boolean canImport() {
		return false;
	}

	@Override
	public void importData() {

	}

	@Override
	public boolean canExport() {
		return false;
	}

	@Override
	public void exportData() {

	}

	@Override
	public String[] getFeatureNames() {
		return null;
	}

	@Override
	protected void initializeGraphics() {

	}

	

}
