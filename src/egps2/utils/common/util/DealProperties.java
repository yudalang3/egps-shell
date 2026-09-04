package egps2.utils.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DealProperties provides shared utility logic for eGPS modules and UI.
 */
public class DealProperties {

	public static Properties getStoredProperties(File inputFile) throws IOException {
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		/// 加载属性列表
		prop.load(in);
		in.close();
		return prop;
	}

	public static void storeProperties(File outfile, String[] keys, String[] values) throws IOException {
		int length = keys.length;
		
		if (length != values.length) {
			throw new IllegalArgumentException("You need to input equal length!");
		}
		Properties prop = new Properties();
		/// 保存属性到b.properties文件
		FileOutputStream oFile = new FileOutputStream(outfile, false);// true表示追加打开
		for (int i = 0; i < length; i++) {
			prop.setProperty(keys[i], values[i]);
		}
		prop.store(oFile, "EGPS v1.6 heatmap!");
		oFile.close();
	}
}
