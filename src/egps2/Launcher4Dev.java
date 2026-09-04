package egps2;

/**
 * Development mode launcher for eGPS application.
 * This launcher enables development mode by setting the isDev flag before launching the main application.
 * Development mode typically enables additional debugging features, logging, or development-specific configurations.
 *
 * @see Launcher
 */
public class Launcher4Dev extends Launcher{

	
	public static void main(String[] args) throws Exception {

		Launcher.isDev = true;
		Launcher.main(args);
	}

}