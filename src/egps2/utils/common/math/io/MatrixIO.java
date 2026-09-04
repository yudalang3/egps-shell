package egps2.utils.common.math.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import egps2.utils.common.model.datatransfer.ThreeTuple;

/**
 * MatrixIO provides shared utility logic for eGPS modules and UI.
 */
public class MatrixIO {
	
	public static ThreeTuple<double[][], String[], String[]> readTXTFile(String csvFileName) throws IOException {
		
		List<String> lines = null;
		  try {
			  lines  = FileUtils.readLines(new File(csvFileName), "UTF-8");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	    int totalRows = lines.size()  - 1;
	    String string = lines.get(0) ;
	    String[] columnNames = string.split("\t");
	    int totalColumns = columnNames.length - 1;
	    
	    double[][] matrix = new double[totalRows][totalColumns];
	    String[] rowNames = new String[totalRows]; 
	    for (int i = 0; i < totalRows; i++) {
			string = lines.get(i + 1);
			 String[] split = string.split("\t");
			 rowNames[i] = split[0];
			 double[] da = new double[totalColumns];
			 
			 for (int j = 0; j < totalColumns; j++) {
				 da[j] = Double.parseDouble(split[j + 1]);
			}
			 
			 matrix[i] = da;
		}
	    
	    return new ThreeTuple<double[][], String[], String[]>(matrix,rowNames,columnNames);
	}
	

}
